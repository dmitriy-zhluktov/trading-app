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
            final var balance = dal.retrieveBalance(coin);
            final var maxOrderQty = scaleTo(instruments.maxOrderQty, instruments.basePrecision);
            if (balance.compareTo(instruments.minOrderQty) < 1) {
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
                            final var newBalance = scaleTo(dal.retrieveBalance(coin), instruments.basePrecision);
                            dal.createTpOrder(
                                    coin,
                                    quote,
                                    scaleTo(percentUp(order.avgPrice, tradeProperties.priceDiffPercent), instruments.tickSize),
                                    newBalance.min(maxOrderQty));
                        } else {
                            attemptsCount--;
                            Thread.sleep(100L);
                        }
                    }
                }
            } else {
                final var buyPrice = dal.retrieveBuyPrice(coin, quote);
                final var currentPrice = dal.retrieveLastPrice(coin, quote);
                if (currentPrice.compareTo(percentUp(buyPrice, tradeProperties.priceDiffPercent)) > 0) {
                    final var scaledBalance = scaleTo(balance, instruments.basePrecision);
                    dal.sell(coin, quote, maxOrderQty.min(scaledBalance));
                }
            }
        }
    }

    private BigDecimal percentUp(
            final BigDecimal value,
            final int percent
    ) {
        final int valueScale = value.scale();
        return value.multiply(BigDecimal.ONE.add(BigDecimal.valueOf(percent).divide(ONE_HUNDRED)))
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

