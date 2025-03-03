package ru.arc.dal.impl;

import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.TradeOrderType;
import com.bybit.api.client.domain.account.AccountType;
import com.bybit.api.client.domain.account.request.AccountDataRequest;
import com.bybit.api.client.domain.market.MarketInterval;
import com.bybit.api.client.domain.market.request.MarketDataRequest;
import com.bybit.api.client.domain.trade.OrderFilter;
import com.bybit.api.client.domain.trade.Side;
import com.bybit.api.client.domain.trade.request.BatchOrderRequest;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;
import com.bybit.api.client.restApi.BybitApiAccountRestClient;
import com.bybit.api.client.restApi.BybitApiMarketRestClient;
import com.bybit.api.client.restApi.BybitApiTradeRestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.arc.dal.TradeDal;
import ru.arc.service.model.Candle;
import ru.arc.service.model.CoinTicker;
import ru.arc.service.model.Order;
import ru.arc.service.model.OrderResult;
import ru.arc.service.model.SpotCoinInstruments;
import ru.arc.service.model.TradeHistory;
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
    public BigDecimal retrieveAvailableBalance(final String coin) {
        final var rq = AccountDataRequest.builder()
                .accountType(AccountType.UNIFIED)
                .coins(coin)
                .build();
        final var rs = accountClient.getWalletBalance(rq);
        final var result = ((LinkedHashMap<?, ?>) rs).get("result");
        final var walletList = ((LinkedHashMap<?, ?>) result).get("list");
        final var walletBalance = objectMapper.convertValue(((List<?>) walletList).get(0), WalletBalance.class);
        return walletBalance.coin.stream()
                .filter(coinBalance -> coinBalance.coin.equals(coin) && coinBalance.walletBalance != null && coinBalance.locked != null)
                .findFirst()
                .map(coinBalance -> coinBalance.walletBalance.subtract(coinBalance.locked))
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public WalletBalance.CoinBalance retrieveCoinBalance(final String coin) {
        final var rq = AccountDataRequest.builder()
                .accountType(AccountType.UNIFIED)
                .coins(coin)
                .build();
        final var rs = accountClient.getWalletBalance(rq);
        final var result = ((LinkedHashMap<?, ?>) rs).get("result");
        final var walletList = ((LinkedHashMap<?, ?>) result).get("list");
        final var walletBalance = objectMapper.convertValue(((List<?>) walletList).get(0), WalletBalance.class);
        return walletBalance.coin.get(0);
    }

    @Override
    public void sell(
            final String coin,
            final String quote,
            final BigDecimal qty
    ) {
        final var order = TradeOrderRequest.builder()
                .orderType(TradeOrderType.MARKET)
                .category(CategoryType.SPOT)
                .symbol(coin + quote)
                .side(Side.SELL)
                .qty(qty.toString())
                .build();
        System.out.println(tradeClient.createOrder(order));
    }

    @Override
    public OrderResult buy(
            final String coin,
            final String quote,
            final BigDecimal usdtAmount
    ) {
        final var order = TradeOrderRequest.builder()
                .orderType(TradeOrderType.MARKET)
                .category(CategoryType.SPOT)
                .symbol(coin + quote)
                .side(Side.BUY)
                .marketUnit("quoteCoin")
                .qty(usdtAmount.toString())
                .build();
        final var rs = tradeClient.createOrder(order);
        System.out.println(rs);
        final var result = ((LinkedHashMap<?, ?>) rs).get("result");
        final var orderResult = OrderResult.builder();
        orderResult.code(Integer.parseInt(((LinkedHashMap<?, ?>) rs).get("retCode").toString()));
        orderResult.msg(((LinkedHashMap<?, ?>) rs).get("retMsg").toString());
        orderResult.orderId(((LinkedHashMap<?, ?>) result).get("orderId").toString());
        orderResult.orderLinkId(((LinkedHashMap<?, ?>) result).get("orderLinkId").toString());

        return orderResult.build();
    }

    @Override
    public BigDecimal retrieveBuyPrice(
            final String coin,
            final String quote
    ) {
        final var rq = TradeOrderRequest.builder()
                .category(CategoryType.SPOT)
                .symbol(coin + quote)
                .build();
        final var rs = tradeClient.getTradeHistory(rq);
            final var result = ((LinkedHashMap<?, ?>) rs).get("result");
            final var tradeList = ((LinkedHashMap<?, ?>) result).get("list");
            final var lastTrade = ((List<?>) tradeList)
                    .stream()
                    .map(item -> objectMapper.convertValue(item, TradeHistory.class))
                    .filter(order -> order.side.equals(Side.BUY.getTransactionSide()))
                    .findFirst();
        return lastTrade.map(trade -> trade.execPrice).orElse(null);
    }

    @Override
    public Order retrieveOrder(final String orderId) {
        final var rq = TradeOrderRequest.builder()
                .category(CategoryType.SPOT)
                .orderId(orderId)
                .build();
        final var rs = tradeClient.getOrderHistory(rq);
        System.out.println(rs);
        final var result = ((LinkedHashMap<?, ?>) rs).get("result");
        final var orderList = (List<?>) ((LinkedHashMap<?, ?>) result).get("list");
        return orderList.isEmpty() ? Order.builder().build() : objectMapper.convertValue(orderList.get(0), Order.class);
    }

    @Override
    public List<Order> retrieveOpenOrders(
            final String coin,
            final String quote
    ) {
        final var rq = TradeOrderRequest.builder()
                .category(CategoryType.SPOT);
        if (coin != null && quote != null) {
                rq.symbol(coin + quote);
        }
        final var rs = tradeClient.getOpenOrders(rq.build());
        System.out.println(rs);
        final var result = ((LinkedHashMap<?, ?>) rs).get("result");
        final var orderList = (List<?>) ((LinkedHashMap<?, ?>) result).get("list");
        return orderList
                .stream()
                .map(order -> objectMapper.convertValue(order, Order.class))
                .toList();
    }

    @Override
    public void cancelOrder(String orderId, String coin, String quote) {
        final var rq = TradeOrderRequest.builder()
                .category(CategoryType.SPOT)
                .symbol(coin + quote)
                .orderId(orderId)
                .build();
        final var rs = tradeClient.cancelOrder(rq);
        System.out.println(rs);
    }

    @Override
    public void createTpSlConditionalOrders(
                final String coin,
                final String quote,
                final BigDecimal tpPrice,
                final BigDecimal slPrice,
                final BigDecimal quantity
    ) {
        final var tpOrder = TradeOrderRequest.builder()
                .orderType(TradeOrderType.MARKET)
                .category(CategoryType.SPOT)
                .orderFilter(OrderFilter.STOP_ORDER)
                .symbol(coin + quote)
                .side(Side.SELL)
                .triggerPrice(tpPrice.toString())
                .qty(quantity.toString())
                .build();

        final var slOrder = TradeOrderRequest.builder()
                .orderType(TradeOrderType.MARKET)
                .category(CategoryType.SPOT)
                .orderFilter(OrderFilter.STOP_ORDER)
                .symbol(coin + quote)
                .side(Side.SELL)
                .triggerPrice(slPrice.toString())
                .qty(quantity.toString())
                .build();
        final var batch = BatchOrderRequest.builder()
                .category(CategoryType.SPOT)
                .request(List.of(slOrder, tpOrder))
                .build();
        System.out.println(tradeClient.createBatchOrder(batch));
    }

    @Override
    public BigDecimal retrieveLastPrice(
            final String coin,
            final String quote
    ) {
        final var rq = MarketDataRequest.builder()
                .category(CategoryType.SPOT)
                .symbol(coin + quote)
                .build();
        final var rs = marketClient.getMarketTickers(rq);
        final var result = ((LinkedHashMap<?, ?>) rs).get("result");
        final var tickerList = ((LinkedHashMap<?, ?>) result).get("list");
        final var coinTicker = objectMapper.convertValue(((List<?>) tickerList).get(0), CoinTicker.class);
        return coinTicker.lastPrice;
    }

    @Override
    public SpotCoinInstruments retrieveSpotInstruments(
            final String coin,
            final String quote
    ) {
        final var rq = MarketDataRequest.builder()
                .category(CategoryType.SPOT)
                .symbol(coin + quote)
                .build();
        final var rs = marketClient.getInstrumentsInfo(rq);
        final var result = ((LinkedHashMap<?, ?>) rs).get("result");
        final var instrumentsList = (List<?>) ((LinkedHashMap<?, ?>) result).get("list");
        if (instrumentsList.isEmpty()) {
            return null;
        }

        final var element = (LinkedHashMap<?, ?>) instrumentsList.get(0);
        final var lotSizeFilter = (LinkedHashMap<?, ?>) element.get("lotSizeFilter");
        final var priceFilter = (LinkedHashMap<?, ?>) element.get("priceFilter");
        final var instruments = SpotCoinInstruments.builder()
                .symbol(element.get("symbol").toString())
                .basePrecision(new BigDecimal(lotSizeFilter.get("basePrecision").toString()))
                .quotePrecision(new BigDecimal(lotSizeFilter.get("quotePrecision").toString()))
                .minOrderQty(new BigDecimal(lotSizeFilter.get("minOrderQty").toString()))
                .maxOrderQty(new BigDecimal(lotSizeFilter.get("maxOrderQty").toString()))
                .minOrderAmt(new BigDecimal(lotSizeFilter.get("minOrderAmt").toString()))
                .maxOrderAmt(new BigDecimal(lotSizeFilter.get("maxOrderAmt").toString()))
                .tickSize(new BigDecimal(priceFilter.get("tickSize").toString()))
                .build();
        return instruments;
    }

    @Override
    public WalletBalance retrieveWalletBalance() {
        final var rq = AccountDataRequest.builder()
                .accountType(AccountType.UNIFIED)
                .build();
        final var rs = accountClient.getWalletBalance(rq);
        final var result = ((LinkedHashMap<?, ?>) rs).get("result");
        final var walletList = ((LinkedHashMap<?, ?>) result).get("list");
        return objectMapper.convertValue(((List<?>) walletList).get(0), WalletBalance.class);
    }

    @Override
    public List<Candle> retrieveCandles(
            final String coin,
            final String quote,
            final String interval,
            final Long startTime,
            final Long endTime
    ) {
        final var rq = MarketDataRequest.builder()
                .category(CategoryType.SPOT)
                .symbol(coin + quote)
                .marketInterval(MarketInterval.FIVE_MINUTES)
                .start(startTime)
                .end(endTime)
                .limit(24)
                .build();
        final var rs = marketClient.getMarketLinesData(rq);
        return null;
    }

    private String scalePrice(
            final BigDecimal price
    ) {
        return price.setScale(4, RoundingMode.HALF_UP).toString();
    }

    private String scaleQty(final BigDecimal qty) {
        return qty.setScale(2, RoundingMode.HALF_DOWN).toString();
    }
}
