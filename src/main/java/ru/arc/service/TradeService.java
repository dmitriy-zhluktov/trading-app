package ru.arc.service;

public interface TradeService {

    void performAction(
            String coin,
            String direction
    );
}
