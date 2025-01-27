package ru.arc.service;

import ru.arc.service.model.WalletBalance;

public interface TradeService {

    void performAction(
            String symbol,
            String direction
    );

    void wipe();

    WalletBalance walletBalance();
}
