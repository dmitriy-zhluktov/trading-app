package ru.arc.service;

import ru.arc.service.model.WalletBalance;
import ru.arc.socket.model.CurrencyUpdate;

public interface TradeService {

    void performAction(
            CurrencyUpdate currencyUpdate
    );

    void wipe();

    WalletBalance walletBalance();
}
