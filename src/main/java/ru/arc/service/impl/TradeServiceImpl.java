package ru.arc.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.arc.config.properties.TradeProperties;
import ru.arc.dal.TradeDal;
import ru.arc.service.TradeService;

import java.math.BigDecimal;
import java.math.MathContext;
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
            final String coin,
            final String direction
    ) {
        if (direction.equals(DIRECTION_UP)) {
            final var balance = dal.retrieveBalance(coin).setScale(0, RoundingMode.DOWN);
            if (balance.compareTo(BigDecimal.ZERO) == 0) {
                final var buyOrder = dal.buy(coin, tradeProperties.buyAmountUsdt);
                if (buyOrder.code != 0) {
                    System.out.println(buyOrder.msg);
                } else {
                    boolean orderCompleted = false;
                    int attemptsCount = 30;
                    while (!orderCompleted && attemptsCount > 0) {
                        final var order = dal.retrieveOrder(buyOrder.orderId);
                        if (ORDER_STATUS_FILLED.equals(order.orderStatus)) {
                            orderCompleted = true;
                            final var newBalance = dal.retrieveBalance(coin).setScale(0, RoundingMode.DOWN);
                            dal.createTpOrder(
                                    coin,
                                    percentUp(order.avgPrice, tradeProperties.priceDiffPercent),
                                    newBalance
                            );
                        } else {
                            attemptsCount--;
                            Thread.sleep(100L);
                        }
                    }
                }
            } else {
                final var buyPrice = dal.retrieveBuyPrice(coin);
                final var currentPrice = dal.retrieveLastPrice(coin);
                if (currentPrice.compareTo(percentUp(buyPrice, tradeProperties.priceDiffPercent)) > 0) {
                    dal.sell(coin, currentPrice.multiply(percentOf(balance, tradeProperties.sellAmountPercent)));
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
}

