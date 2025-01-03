package ru.arc.dal.impl;

import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.TradeOrderType;
import com.bybit.api.client.domain.account.AccountType;
import com.bybit.api.client.domain.account.request.AccountDataRequest;
import com.bybit.api.client.domain.trade.Side;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;
import com.bybit.api.client.restApi.BybitApiAccountRestClient;
import com.bybit.api.client.restApi.BybitApiAsyncTradeRestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.arc.dal.TradeDal;
import ru.arc.service.model.WalletBalance;

import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
public class TradeDalImpl implements TradeDal {
    private final BybitApiAsyncTradeRestClient tradeClient;
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
                .filter(coinBalance -> coinBalance.getCoin().equals(coin))
                .findFirst()
                .get()
                .getWalletBalance();
    }

    @Override
    public void sell() {
        final var order = TradeOrderRequest.builder()
                .orderType(TradeOrderType.MARKET)
                .category(CategoryType.SPOT)
                .symbol("CELOUSDT")
                .side(Side.SELL)
                .qty("10.01")
                .build();
        tradeClient.createOrder(order, System.out::println);
    }
}
