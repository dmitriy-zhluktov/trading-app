package ru.arc.dal;

import java.math.BigDecimal;

public interface TradeDal {

    BigDecimal retrieveBalance(String coin);

    void sell(
            String coin,
            BigDecimal usdtAmount
    );

    void buy(
            String coin,
            BigDecimal usdtAmount
    );

    BigDecimal retrieveBuyPrice(String coin);

    void createLimitOrder(
            String coin,
            BigDecimal currentPrice,
            BigDecimal usdtAmount,
            BigDecimal tpLimitPrice,
            BigDecimal slLimitPrice
    );

    BigDecimal retrieveLastPrice(String coin);
}
