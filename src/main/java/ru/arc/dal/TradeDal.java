package ru.arc.dal;

import java.math.BigDecimal;

public interface TradeDal {

    String retrieveBalance(String coin);

    void sell(
            String coin,
            BigDecimal usdtAmount
    );

    void buy(
            String coin,
            BigDecimal usdtAmount
    );

    BigDecimal retrieveBuyPrice(String coin);
}
