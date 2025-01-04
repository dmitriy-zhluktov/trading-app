package ru.arc.service.impl;

import lombok.RequiredArgsConstructor;
import ru.arc.config.properties.TradeProperties;
import ru.arc.dal.TradeDal;
import ru.arc.service.TradeService;

import java.math.BigDecimal;

@RequiredArgsConstructor
public final class TradeServiceImpl implements TradeService {

    private final TradeDal dal;
    private final TradeProperties tradeProperties;
    private static final String DIRECTION_UP = "UP";
    private static final String DIRECTION_DOWN = "DOWN";
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @Override
    public String retrieveBalance(String coin) {
        return dal.retrieveBalance(coin);
    }

    @Override
    public void performAction(String coin, String direction, BigDecimal currentPrice) {
        if (direction.equals(DIRECTION_UP)) {
            final var balance = new BigDecimal(dal.retrieveBalance(coin));
            if (balance.compareTo(BigDecimal.ZERO) == 0) {
                dal.buy(coin, tradeProperties.buyAmountUsdt);
            } else {
                final var buyPrice = dal.retrieveBuyPrice(coin);
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
        return value.multiply(BigDecimal.ONE.add(BigDecimal.valueOf(percent).divide(ONE_HUNDRED)));
    }

    private BigDecimal percentDown(
            final BigDecimal value,
            final int percent
    ) {
        return value.multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(percent).divide(ONE_HUNDRED)));
    }

    private BigDecimal percentOf(
            final BigDecimal value,
            final int percent
    ) {
        return value.multiply(BigDecimal.valueOf(percent)).divide(ONE_HUNDRED);
    }
}

