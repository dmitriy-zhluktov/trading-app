package ru.arc.dao.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record4;
import ru.arc.dao.CandleDao;
import ru.arc.dao.rel.CandlelRel;
import ru.arc.service.model.Candle;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CandleDaoImpl implements CandleDao {
    private final DSLContext dslContext;

    @Override
    public void insert(Candle candle) {
        dslContext.insertInto(CandlelRel.INSTANCE)
                .set(CandlelRel.SYMBOL, candle.symbol)
                .set(CandlelRel.DATETIME, candle.dateTime)
                .set(CandlelRel.HIGH_PRICE, candle.highPrice)
                .set(CandlelRel.LOW_PRICE, candle.lowPrice)
                .onConflictDoNothing()
                .execute();
    }

    @Override
    public void insert(List<Candle> candles) {
        final var query = dslContext.insertInto(
                CandlelRel.INSTANCE,
                CandlelRel.SYMBOL,
                CandlelRel.DATETIME,
                CandlelRel.HIGH_PRICE,
                CandlelRel.LOW_PRICE
                );
        candles.forEach(candle -> {
            query.values(candle.symbol, candle.dateTime, candle.highPrice, candle.lowPrice);
        });
        query.execute();
    }

    @Override
    public List<Candle> retrieveAllCandles() {
        return dslContext.selectFrom(CandlelRel.INSTANCE)
                .fetch(record -> Candle.builder()
                        .symbol(record.get(CandlelRel.SYMBOL))
                        .dateTime(record.get(CandlelRel.DATETIME))
                        .highPrice(record.get(CandlelRel.HIGH_PRICE))
                        .highPrice(record.get(CandlelRel.HIGH_PRICE))
                        .lowPrice(record.get(CandlelRel.LOW_PRICE))
                        .build());
    }
}
