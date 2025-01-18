package ru.arc.dal.impl;

import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.TradeOrderType;
import com.bybit.api.client.domain.account.AccountType;
import com.bybit.api.client.domain.account.request.AccountDataRequest;
import com.bybit.api.client.domain.market.request.MarketDataRequest;
import com.bybit.api.client.domain.trade.Side;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;
import com.bybit.api.client.restApi.BybitApiAccountRestClient;
import com.bybit.api.client.restApi.BybitApiMarketRestClient;
import com.bybit.api.client.restApi.BybitApiTradeRestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.arc.dal.TradeDal;
import ru.arc.service.model.CoinTicker;
import ru.arc.service.model.Order;
import ru.arc.service.model.WalletBalance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
public class TradeDalImpl implements TradeDal {
    private final BybitApiTradeRestClient tradeClient;
    private final BybitApiAccountRestClient accountClient;
    private final BybitApiMarketRestClient marketClient;
    private final ObjectMapper objectMapper;

    @Override
    public BigDecimal retrieveBalance(String coin) {
        final var rq = AccountDataRequest.builder()
                .accountType(AccountType.UNIFIED)
                .coins(coin.toUpperCase())
                .build();
        final var rs = accountClient.getWalletBalance(rq);
        final var result = ((LinkedHashMap<?, ?>) rs).get("result");
        final var walletList = ((LinkedHashMap<?, ?>) result).get("list");
        final var walletBalance = objectMapper.convertValue(((List<?>) walletList).get(0), WalletBalance.class);
        return walletBalance.coin.stream()
                .filter(coinBalance -> coinBalance.coin.equals(coin) && !coinBalance.walletBalance.isBlank())
                .findFirst()
                .map(coinBalance -> new BigDecimal(coinBalance.walletBalance))
                .orElse(BigDecimal.ZERO);
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
                    .filter(order -> order.side.equals(Side.BUY.getTransactionSide()))
                    .findFirst();
        return lastOrder.map((order -> order.execPrice)).orElse(null);
    }

    @Override
    public void createLimitOrder(
            final String coin,
            final BigDecimal currentPrice,
            final BigDecimal usdtAmount,
            final BigDecimal tpLimitPrice,
            final BigDecimal slLimitPrice) {
        final var order = TradeOrderRequest.builder()
                .orderType(TradeOrderType.LIMIT)
                .category(CategoryType.SPOT)
                .symbol(coin + "USDT")
                .price(currentPrice.setScale(4, RoundingMode.HALF_UP).toString())
                .side(Side.BUY)
                //.marketUnit("quoteCoin")
                .tpLimitPrice(tpLimitPrice.setScale(4, RoundingMode.HALF_UP).toString())
                .tpOrderType(TradeOrderType.MARKET)
                .slLimitPrice(slLimitPrice.setScale(4, RoundingMode.HALF_UP).toPlainString())
                .slOrderType(TradeOrderType.MARKET)
                .qty(usdtAmount.setScale(2, RoundingMode.HALF_UP).divide(currentPrice, RoundingMode.HALF_DOWN).toString())
                .build();
        System.out.println(currentPrice.toString());
        System.out.println(tradeClient.createOrder(order));
    }

    @Override
    public BigDecimal retrieveLastPrice(final String coin) {
        final var rq = MarketDataRequest.builder()
                .category(CategoryType.SPOT)
                .symbol(coin + "USDT")
                .build();
        final var rs = marketClient.getMarketTickers(rq);
        final var result = ((LinkedHashMap<?, ?>) rs).get("result");
        final var tickerList = ((LinkedHashMap<?, ?>) result).get("list");
        final var coinTicker = objectMapper.convertValue(((List<?>) tickerList).get(0), CoinTicker.class);
        return coinTicker.lastPrice;
    }
}
