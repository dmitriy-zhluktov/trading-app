package ru.arc.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.arc.config.properties.TradeProperties;
import ru.arc.dal.TradeDal;
import ru.arc.service.TradeService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
public final class TradeServiceImpl implements TradeService {

    private final TradeDal dal;
    private final TradeProperties tradeProperties;
    private static final String DIRECTION_UP = "UP";
    private static final String DIRECTION_DOWN = "DOWN";
    private static final String ORDER_STATUS_FILLED = "Filled";
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @SneakyThrows
    @Override
    public void performAction(
            final String symbol,
            final String direction
    ) {
        if (direction.equalsIgnoreCase(DIRECTION_UP)) {
            final var splitSymbol = symbol.split("/");
            final var coin = splitSymbol[0].toUpperCase();
            final var quote = splitSymbol[1].toUpperCase();
            final var instruments = dal.retrieveSpotInstruments(coin, quote);
            final var coinBalance = dal.retrieveCoinBalance(coin);
            final var coinQty = coinBalance.walletBalance;
            final var maxOrderQty = scaleTo(instruments.maxOrderQty, instruments.basePrecision);
            if (coinQty == null || coinQty.compareTo(instruments.minOrderQty) < 1) {
                final var buyOrder = dal.buy(coin, quote, scaleTo(tradeProperties.buyAmountUsdt, instruments.quotePrecision));
                if (buyOrder.code != 0) {
                    System.out.println(buyOrder.msg);
                } else {
                    boolean orderCompleted = false;
                    int attemptsCount = 30;
                    while (!orderCompleted && attemptsCount > 0) {
                        final var order = dal.retrieveOrder(buyOrder.orderId);
                        if (ORDER_STATUS_FILLED.equals(order.orderStatus)) {
                            orderCompleted = true;
                            final var actualCoinBalance = dal.retrieveCoinBalance(coin);
                            final var availableBalance = scaleTo(
                                    actualCoinBalance.walletBalance.subtract(actualCoinBalance.locked),
                                    instruments.basePrecision
                            );
                            dal.createTpOrder(
                                    coin,
                                    quote,
                                    scaleTo(percentUp(order.avgPrice, tradeProperties.priceDiffPercent), instruments.tickSize),
                                    availableBalance.min(maxOrderQty));
                        } else {
                            attemptsCount--;
                            Thread.sleep(100L);
                        }
                    }
                }
            } else {
                final var priceToSell = percentUp(dal.retrieveBuyPrice(coin, quote), tradeProperties.priceDiffPercent);
                final var currentPrice = dal.retrieveLastPrice(coin, quote);
                dal.createTpOrder(
                        coin,
                        quote,
                        scaleTo(priceToSell.max(currentPrice), instruments.tickSize),
                        scaleTo(coinBalance.walletBalance.subtract(coinBalance.locked), instruments.basePrecision)
                );
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
            final int percent
    ) {
        final int valueScale = value.scale();
        return value.multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(percent).divide(ONE_HUNDRED)))
                .setScale(valueScale, RoundingMode.HALF_DOWN);
    }

    private BigDecimal percentOf(
            final BigDecimal value,
            final int percent
    ) {
        final int valueScale = value.scale();
        return value.multiply(BigDecimal.valueOf(percent)).divide(ONE_HUNDRED)
                .setScale(valueScale, RoundingMode.HALF_DOWN);
    }

    private BigDecimal scaleTo(
            final BigDecimal value,
            final BigDecimal calibrator
    ) {
        return value.setScale(calibrator.scale(), RoundingMode.DOWN);
    }
}

