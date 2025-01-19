package ru.arc.service;

import java.math.BigDecimal;

public interface TradeService {

    void performAction(
            String coin,
            String direction
    );
}
