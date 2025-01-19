package ru.arc.dal;

import ru.arc.service.model.Order;
import ru.arc.service.model.OrderResult;

import java.math.BigDecimal;

public interface TradeDal {

    BigDecimal retrieveBalance(String coin);

    void sell(
            String coin,
            BigDecimal usdtAmount
    );

    OrderResult buy(
            String coin,
            BigDecimal usdtAmount
    );

    BigDecimal retrieveBuyPrice(String coin);

    Order retrieveOrder(String orderId);

    void createTpOrder(
            String coin,
            BigDecimal tpPrice,
            BigDecimal quantity
    );

    BigDecimal retrieveLastPrice(String coin);
}
