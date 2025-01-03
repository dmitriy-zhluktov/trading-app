package ru.arc.dal;

public interface TradeDal {

    String retrieveBalance(String coin);

    void sell();
}
