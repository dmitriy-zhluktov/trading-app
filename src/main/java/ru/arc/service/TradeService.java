package ru.arc.service;

import java.math.BigDecimal;

public interface TradeService {

    String retrieveBalance(String coin);

    void performAction(
            String coin,
            String direction,
            BigDecimal currentPrice
    );
}
