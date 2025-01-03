package ru.arc.service.impl;

import lombok.RequiredArgsConstructor;
import ru.arc.dal.TradeDal;
import ru.arc.service.TradeService;

@RequiredArgsConstructor
public final class TradeServiceImpl implements TradeService {

    private final TradeDal dal;

    @Override
    public String retrieveBalance(String coin) {
        return dal.retrieveBalance(coin);
    }

    @Override
    public void sell(final String message) {
        dal.sell();
    }
}

