package ru.arc.dal;

import ru.arc.service.model.Order;
import ru.arc.service.model.OrderResult;
import ru.arc.service.model.SpotCoinInstruments;
import ru.arc.service.model.WalletBalance;

import java.math.BigDecimal;
import java.util.List;

public interface TradeDal {

    BigDecimal retrieveAvailableBalance(String coin);

    WalletBalance.CoinBalance retrieveCoinBalance(String coin);

    void sell(
            String coin,
            String quote,
            BigDecimal qty
    );

    OrderResult buy(
            String coin,
            String quote,
            BigDecimal usdtAmount
    );

    BigDecimal retrieveBuyPrice(
            String coin,
            String quote
    );

    Order retrieveOrder(String orderId);

    List<Order> retrieveOpenOrders(
            String coin,
            String quote
    );

    void cancelOrder(
            String orderId,
            String coin,
            String quote
    );

    void createTpSlConditionalOrders(
            String coin,
            String quote,
            BigDecimal tpPrice,
            BigDecimal slPrice,
            BigDecimal quantity
    );

    BigDecimal retrieveLastPrice(
            String coin,
            String quote
    );

    SpotCoinInstruments retrieveSpotInstruments(
            String coin,
            String quote
    );

    WalletBalance retrieveWalletBalance();
}
