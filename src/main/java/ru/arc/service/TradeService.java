package ru.arc.service;

public interface TradeService {

    void performAction(
            String symbol,
            String direction
    );
}
