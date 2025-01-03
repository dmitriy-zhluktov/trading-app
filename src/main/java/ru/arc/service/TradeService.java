package ru.arc.service;

public interface TradeService {

    String retrieveBalance(String coin);

    void sell(String message);
}
