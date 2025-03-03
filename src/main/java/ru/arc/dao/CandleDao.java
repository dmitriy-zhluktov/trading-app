package ru.arc.dao;

import ru.arc.service.model.Candle;
import ru.arc.service.model.Signal;
import ru.arc.socket.model.CurrencyUpdate;

import java.util.List;

public interface CandleDao {

    void insert(Candle candle);

    void insert(List<Candle> candles);

    List<Candle> retrieveAllCandles();
}
