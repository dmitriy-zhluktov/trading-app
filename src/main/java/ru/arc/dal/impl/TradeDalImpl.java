package ru.arc.dal.impl;

import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.TradeOrderType;
import com.bybit.api.client.domain.account.AccountType;
import com.bybit.api.client.domain.account.request.AccountDataRequest;
import com.bybit.api.client.domain.trade.Side;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;
import com.bybit.api.client.restApi.BybitApiAccountRestClient;
import com.bybit.api.client.restApi.BybitApiTradeRestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.arc.dal.TradeDal;
import ru.arc.service.model.Order;
import ru.arc.service.model.WalletBalance;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
public class TradeDalImpl implements TradeDal {
    private final BybitApiTradeRestClient tradeClient;
    private final BybitApiAccountRestClient accountClient;
    private final ObjectMapper objectMapper;

    @Override
    public String retrieveBalance(String coin) {
        final var rq = AccountDataRequest.builder()
                .accountType(AccountType.UNIFIED)
                .coins(coin.toUpperCase())
                .build();
        final var rs = accountClient.getWalletBalance(rq);
        final var result = ((LinkedHashMap<?, ?>) rs).get("result");
        final var walletList = ((LinkedHashMap<?, ?>) result).get("list");
        final var walletBalance = objectMapper.convertValue(((List<?>) walletList).get(0), WalletBalance.class);
        return walletBalance.getCoin().stream()
                .filter(coinBalance -> coinBalance.getCoin().equals(coin) && !coinBalance.getWalletBalance().isBlank())
                .findFirst()
                .map(WalletBalance.CoinBalance::getWalletBalance)
                .orElse("0");
    }

    @Override
    public void sell(
            final String coin,
            final BigDecimal usdtAmount
    ) {
        final var order = TradeOrderRequest.builder()
                .orderType(TradeOrderType.MARKET)
                .category(CategoryType.SPOT)
                .symbol(coin + "USDT")
                .side(Side.SELL)
                .marketUnit("quoteCoin")
                .qty(usdtAmount.toString())
                .build();
        System.out.println(tradeClient.createOrder(order));
    }

    @Override
    public void buy(
            final String coin,
            final BigDecimal usdtAmount
    ) {
        final var order = TradeOrderRequest.builder()
                .orderType(TradeOrderType.MARKET)
                .category(CategoryType.SPOT)
                .symbol(coin + "USDT")
                .side(Side.BUY)
                .marketUnit("quoteCoin")
                .qty(usdtAmount.toString())
                .build();
        System.out.println(tradeClient.createOrder(order));
    }

    @Override
    public BigDecimal retrieveBuyPrice(String coin) {
        final var rq = TradeOrderRequest.builder()
                .category(CategoryType.SPOT)
                .symbol(coin + "USDT")
                .openOnly(0)
                .build();
        final var rs = tradeClient.getTradeHistory(rq);
            final var result = ((LinkedHashMap<?, ?>) rs).get("result");
            final var tradeList = ((LinkedHashMap<?, ?>) result).get("list");
            final var lastOrder = ((List<?>) tradeList)
                    .stream()
                    .map(item -> objectMapper.convertValue(item, Order.class))
                    .filter(order -> order.getSide().equals(Side.BUY.getTransactionSide()))
                    .findFirst();
        return lastOrder.map(Order::getExecPrice).orElse(null);
    }
}
