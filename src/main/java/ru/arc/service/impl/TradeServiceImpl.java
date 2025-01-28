package ru.arc.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.arc.config.properties.TradeProperties;
import ru.arc.dal.TradeDal;
import ru.arc.service.TradeService;
import ru.arc.service.model.WalletBalance;
import ru.arc.socket.model.CurrencyUpdate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
public final class TradeServiceImpl implements TradeService {

    private final TradeDal dal;
    private final TradeProperties tradeProperties;
    private static final String DIRECTION_UP = "UP";
    private static final String DIRECTION_DOWN = "DOWN";
    private static final String ORDER_STATUS_FILLED = "Filled";
    private static final String ORDER_STATUS_UNTRIGGERED = "Untriggered";
    private static final String STOP_ORDER_TYPE_OPTIONAL = "Stop";
    private static final String ORDER_SIDE_SELL = "Sell";
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @SneakyThrows
    @Override
    public void performAction(final CurrencyUpdate currencyUpdate) {
        if (DIRECTION_UP.equalsIgnoreCase(currencyUpdate.direction)) {
            upAction(currencyUpdate);
        } else {
            downAction(currencyUpdate);
        }
    }

    @Override
    public void wipe() {
        final var openOrders = dal.retrieveOpenOrders(null, null);
        openOrders.forEach(order -> dal.cancelOrder(order.orderId, order.symbol, ""));
        final var balance = dal.retrieveWalletBalance();
        balance.coin.forEach(coinBalance -> {
            if (!coinBalance.coin.equals("USDT")) {
                final var instruments = dal.retrieveSpotInstruments(coinBalance.coin, "USDT");
                if (instruments.minOrderQty.compareTo(coinBalance.walletBalance) < 1) {
                    dal.sell(coinBalance.coin, "USDT", scaleTo(coinBalance.walletBalance, instruments.basePrecision));
                }
            }
        });
    }

    @Override
    public WalletBalance walletBalance() {
        return dal.retrieveWalletBalance();
    }

    @SneakyThrows
    private void upAction(final CurrencyUpdate currencyUpdate) {
        final var splitSymbol = currencyUpdate.symbol.split("/");
        final var coin = splitSymbol[0].toUpperCase();
        final var quote = splitSymbol[1].toUpperCase();
        final var instruments = dal.retrieveSpotInstruments(coin, quote);
        final var coinBalance = dal.retrieveCoinBalance(coin);
        final var coinQty = coinBalance.walletBalance;
        final var maxOrderQty = scaleTo(instruments.maxOrderQty, instruments.basePrecision);
        final var openOrders = dal.retrieveOpenOrders(coin, quote);
        final var optionalTpSlOrders = openOrders
                .stream()
                .filter(order -> ORDER_STATUS_UNTRIGGERED.equalsIgnoreCase(order.orderStatus) &&
                        STOP_ORDER_TYPE_OPTIONAL.equalsIgnoreCase(order.stopOrderType) &&
                        ORDER_SIDE_SELL.equalsIgnoreCase(order.side))
                .toList();
        // Если размер меньше 2, значит ранее сработал один из ордеров, значит надо отменить другой
        if (optionalTpSlOrders.size() < 2 && (coinQty == null || coinQty.compareTo(instruments.minOrderQty) < 1)) {
            optionalTpSlOrders.forEach( order -> dal.cancelOrder(order.orderId, coin, quote));
            final var buyOrder = dal.buy(coin, quote, scaleTo(tradeProperties.buyAmountUsdt, instruments.quotePrecision));
            if (buyOrder.code != 0) {
                System.out.println(buyOrder.msg);
            } else {
                boolean orderCompleted = false;
                int attemptsCount = 30;
                while (!orderCompleted && attemptsCount > 0) {
                    final var order = dal.retrieveOrder(buyOrder.orderId);
                    if (ORDER_STATUS_FILLED.equalsIgnoreCase(order.orderStatus)) {
                        orderCompleted = true;
                        final var actualCoinBalance = dal.retrieveCoinBalance(coin);
                        final var availableBalance = scaleTo(
                                actualCoinBalance.walletBalance.subtract(actualCoinBalance.locked),
                                instruments.basePrecision
                        );
                        dal.createTpSlConditionalOrders(
                                coin,
                                quote,
                                scaleTo(percentUp(order.avgPrice, tradeProperties.takeProfitPercent), instruments.tickSize),
                                scaleTo(percentDown(order.avgPrice, tradeProperties.stopLossPercent), instruments.tickSize),
                                availableBalance.min(maxOrderQty));
                    } else {
                        attemptsCount--;
                        Thread.sleep(100L);
                    }
                }
            }
        } else {
            if (optionalTpSlOrders.isEmpty()) {
                final var buyPrice = dal.retrieveBuyPrice(coin, quote);
                final var tpPrice = percentUp(buyPrice, tradeProperties.takeProfitPercent);
                final var slPrice = percentDown(buyPrice, tradeProperties.stopLossPercent);
                final var currentPrice = dal.retrieveLastPrice(coin, quote);
                dal.createTpSlConditionalOrders(
                        coin,
                        quote,
                        scaleTo(tpPrice.max(currentPrice), instruments.tickSize),
                        scaleTo(slPrice.min(currentPrice), instruments.tickSize),
                        scaleTo(coinBalance.walletBalance.subtract(coinBalance.locked), instruments.basePrecision));
            }
        }
    }

    @SneakyThrows
    private void downAction(final CurrencyUpdate currencyUpdate) {
        final var splitSymbol = currencyUpdate.symbol.split("/");
        final var coin = splitSymbol[0].toUpperCase();
        final var quote = splitSymbol[1].toUpperCase();
        final var coinBalance = dal.retrieveCoinBalance(coin);
        final var instruments = dal.retrieveSpotInstruments(coin, quote);
        final var coinQty = coinBalance.walletBalance;
        if (coinQty == null || coinQty.compareTo(instruments.minOrderQty) < 1) {
            final var buyOrder = dal.buy(coin, quote, scaleTo(tradeProperties.buyAmountUsdt, instruments.quotePrecision));
            if (buyOrder.code != 0) {
                System.out.println(buyOrder.msg);
            } else {
                boolean orderCompleted = false;
                int attemptsCount = 30;
                while (!orderCompleted && attemptsCount > 0) {
                    final var order = dal.retrieveOrder(buyOrder.orderId);
                    if (ORDER_STATUS_FILLED.equalsIgnoreCase(order.orderStatus)) {
                        orderCompleted = true;
                        final var actualCoinBalance = dal.retrieveCoinBalance(coin);
                        final var availableBalance = scaleTo(
                                actualCoinBalance.walletBalance.subtract(actualCoinBalance.locked),
                                instruments.basePrecision
                        );
                        final var maxOrderQty = scaleTo(instruments.maxOrderQty, instruments.basePrecision);
                        final var trendDownPercent = currencyUpdate.trendLowerBound.divide(currencyUpdate.lastClose, RoundingMode.FLOOR).subtract(BigDecimal.ONE).multiply(ONE_HUNDRED);
                        final var tpPercent = percentOf(trendDownPercent, tradeProperties.downRecoverPercent);
                        dal.createTpSlConditionalOrders(
                                coin,
                                quote,
                                scaleTo(percentUp(order.avgPrice, tpPercent), instruments.tickSize),
                                scaleTo(percentDown(order.avgPrice, tradeProperties.stopLossPercent), instruments.tickSize),
                                availableBalance.min(maxOrderQty));
                    } else {
                        attemptsCount--;
                        Thread.sleep(100L);
                    }
                }
            }
        }
    }

    private BigDecimal percentUp(
            final BigDecimal value,
            final BigDecimal percent
    ) {
        final int valueScale = value.scale();
        return value.multiply(BigDecimal.ONE.add(percent.divide(ONE_HUNDRED)))
                .setScale(valueScale, RoundingMode.HALF_DOWN);
    }

    private BigDecimal percentDown(
            final BigDecimal value,
            final BigDecimal percent
    ) {
        final int valueScale = value.scale();
        return value.multiply(BigDecimal.ONE.subtract(percent.divide(ONE_HUNDRED)))
                .setScale(valueScale, RoundingMode.HALF_DOWN);
    }

    private BigDecimal percentOf(
            final BigDecimal value,
            final BigDecimal percent
    ) {
        final int valueScale = value.scale();
        return value.multiply(percent).divide(ONE_HUNDRED)
                .setScale(valueScale, RoundingMode.HALF_DOWN);
    }

    private BigDecimal scaleTo(
            final BigDecimal value,
            final BigDecimal calibrator
    ) {
        return value.setScale(calibrator.scale(), RoundingMode.DOWN);
    }
}

