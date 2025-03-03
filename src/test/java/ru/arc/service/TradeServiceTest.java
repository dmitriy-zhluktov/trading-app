package ru.arc.service;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.market.MarketInterval;
import com.bybit.api.client.domain.market.request.MarketDataRequest;
import com.bybit.api.client.restApi.BybitApiMarketRestClient;
import com.bybit.api.client.service.BybitApiClientFactory;
import lombok.Builder;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import ru.arc.config.DaoConfig;
import ru.arc.config.DatabaseTestConfig;
import ru.arc.dao.CandleDao;
import ru.arc.dao.SignalDao;
import ru.arc.service.model.Candle;
import ru.arc.service.model.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TradeServiceTest {
    private final DatabaseTestConfig dbConfig = new DatabaseTestConfig();
    private final DSLContext dslContext = dbConfig.dslContext();
    private final DaoConfig daoConfig = new DaoConfig();
    private final SignalDao signalDao = daoConfig.signalDao(dslContext);
    private final CandleDao candleDao = daoConfig.candleDao(dslContext);
    private final BybitApiMarketRestClient marketClient = BybitApiClientFactory.newInstance(
                    "To8hftWm1ItfokqbpQ",
                    "zJGWgeRawLdkx1VN50ZGQ8vqbl84chnho2ho",
                    BybitApiConfig.DEMO_TRADING_DOMAIN,
                    true
            )
            .newMarketDataRestClient();


    //@Test
    public void readCsv() {
        String fileName= "data.csv";
        File file= new File(getClass().getClassLoader().getResource(fileName).getFile());

        // this gives you a 2-dimensional array of strings
        List<List<String>> lines = new ArrayList<>();
        Scanner inputStream;

        try{
            inputStream = new Scanner(file);

            while(inputStream.hasNext()){
                String line= inputStream.next();
                String[] values = line.split(",");
                // this adds the currently parsed line to the 2-dimensional string array
                lines.add(Arrays.asList(values));
            }

            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        var tradeHistory = new ArrayList<TestHistory>();

        for (List<String> line: lines) {
            final var history = TestHistory.builder()
                    .symbol(line.get(0))
                    .execPrice(new BigDecimal(line.get(1)))
                    .execTime(LocalDateTime.parse(line.get(5)))
                    .direction(line.get(7))
                    .changePercentage(new BigDecimal(line.get(8)))
                    .build();
            tradeHistory.add(history);
        }
        tradeHistory.forEach(item -> {
            final var rq = MarketDataRequest.builder()
                    .category(CategoryType.SPOT)
                    .symbol(item.symbol.split("_")[0])
                    .marketInterval(MarketInterval.FIVE_MINUTES)
                    .start(item.execTime.minusHours(6).toInstant(ZoneOffset.UTC).toEpochMilli())
                    .end(null)
                    .limit(288)
                    .build();
            final var rs = marketClient.getMarketLinesData(rq);
            final var result = ((LinkedHashMap<?, ?>) rs).get("result");
            final var candleList = (List<List<String>>) ((LinkedHashMap<?, ?>) result).get("list");
            final var candles = candleList.stream()
                    .map(elem ->
                            Candle.builder()
                                    .symbol(item.symbol)
                                    .dateTime(Long.valueOf(elem.get(0)))
                                    .openPrice(new BigDecimal(elem.get(1)))
                                    .highPrice(new BigDecimal(elem.get(2)))
                                    .lowPrice(new BigDecimal(elem.get(3)))
                                    .closePrice(new BigDecimal(elem.get(4)))
                                    .build()
                    )
                    .toList();
            try (PrintWriter pw = new PrintWriter("candles/" + item.symbol + ".csv", StandardCharsets.UTF_8)) {
                candles.forEach(candle -> {
                    final var data = new String[] {candle.dateTime.toString(), candle.highPrice.toString(), candle.lowPrice.toString()};
                    pw.println(String.join(",", data));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void fillCandles() {
        final var signals = signalDao.retrieveAllSignals().stream().sorted(Comparator.comparing(s -> s.symbol)).toList();
        System.out.println(signals.size());
        final var dateTimeRanges = new HashMap<String, Pair<OffsetDateTime, OffsetDateTime>>();
        final var signalsGroupedBySymbol = signals.stream()
        .collect(Collectors.groupingBy(s -> s.symbol));
        signalsGroupedBySymbol.values()
                .forEach( signalsList -> {
                    signalsList.sort(Comparator.comparing(s -> s.triggerDate));
                    dateTimeRanges.put(signalsList.get(0).symbol, Pair.of(signalsList.get(0).triggerDate, signalsList.get(signalsList.size() - 1).triggerDate));
                });
        System.out.println(dateTimeRanges.size());

        dateTimeRanges.forEach((symbol, dates) -> {
            final var rqSymbol = symbol.replace("/", "");
            final var startTime = dates.left.toInstant().toEpochMilli();
            final var endTime = dates.right.plusHours(24).toInstant().toEpochMilli();
            final var iterationsCount = (int) (endTime - startTime)/60000000;
            for (int i = 0; i <= iterationsCount; i++) {
                final var rq = MarketDataRequest.builder()
                        .category(CategoryType.SPOT)
                        .symbol(rqSymbol)
                        .marketInterval(MarketInterval.ONE_MINUTE)
                        .start(startTime + i*60000000)
                        .end(null)
                        .limit(1000)
                        .build();
                final var rs = marketClient.getMarketLinesData(rq);
                final var result = ((LinkedHashMap<?, ?>) rs).get("result");
                final var candleList = (List<List<String>>) ((LinkedHashMap<?, ?>) result).get("list");
                final var candles = candleList.stream()
                        .map(elem ->
                                Candle.builder()
                                        .symbol(rqSymbol)
                                        .dateTime(Long.valueOf(elem.get(0)))
                                        .openPrice(new BigDecimal(elem.get(1)))
                                        .highPrice(new BigDecimal(elem.get(2)))
                                        .lowPrice(new BigDecimal(elem.get(3)))
                                        .closePrice(new BigDecimal(elem.get(4)))
                                        .build()
                        )
                        .toList();
                System.out.println(candles.size());
                candleDao.insert(candles);
            }
        });


    }

    private List<Candle> tops(final List<Candle> candles) {
        final var tops = new ArrayList<Candle>();
        if (candles == null || candles.size() < 3) {
            System.out.println("candles should have at least 3 elements");
            return tops;
        }

        for (int i = 1; i < candles.size() - 1; i++) {
            if (candles.get(i).closePrice.compareTo(candles.get(i - 1).closePrice) >= 0
                    && candles.get(i).closePrice.compareTo(candles.get(i + 1).closePrice) >= 0) {
                tops.add(candles.get(i));
            }
        }
        return tops;
    }

    private List<Candle> lows(final List<Candle> candles) {
        final var lows = new ArrayList<Candle>();
        if (candles == null || candles.size() < 3) {
            System.out.println("candles should have at least 3 elements");
            return lows;
        }

        for (int i = 1; i < candles.size() - 1; i++) {

            if (candles.get(i).openPrice.compareTo(candles.get(i - 1).openPrice) < 0
                    && candles.get(i).openPrice.compareTo(candles.get(i + 1).openPrice) < 0) {
                lows.add(candles.get(i));
            }
        }
        return lows;
    }

    @Builder
    static class TestHistory {
        public final String symbol;
        public final BigDecimal execPrice;
        public final LocalDateTime execTime;
        public final String direction;
        public final BigDecimal changePercentage;
    }
}
