package ru.arc.dao;

import ru.arc.service.model.Signal;
import ru.arc.socket.model.CurrencyUpdate;

import java.util.List;

public interface SignalDao {

    void insert(CurrencyUpdate currencyUpdate);

    List<Signal> retrieveAllSignals();
}
