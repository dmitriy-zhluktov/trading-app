package ru.arc.dao.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.transaction.annotation.Transactional;
import ru.arc.dao.SignalDao;
import ru.arc.dao.rel.SignalRel;
import ru.arc.service.model.Signal;
import ru.arc.socket.model.CurrencyUpdate;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
public class SignalDaoImpl implements SignalDao {

    private final DSLContext dslContext;

    @Transactional
    @Override
    public void insert(CurrencyUpdate currencyUpdate) {
        dslContext.insertInto(SignalRel.INSTANCE)
                .set(SignalRel.SYMBOL, currencyUpdate.symbol)
                .set(SignalRel.DIRECTION, currencyUpdate.direction)
                .set(SignalRel.TRIGGER_DATE, OffsetDateTime.now())
                .set(SignalRel.TREND_UPPER_BOUND, currencyUpdate.trendUpperBound)
                .set(SignalRel.TREND_LOWER_BOUND, currencyUpdate.trendLowerBound)
                .set(SignalRel.LAST_CLOSE, currencyUpdate.lastClose)
                .onConflictDoNothing()
                .execute();
    }

    @Override
    public List<Signal> retrieveAllSignals() {
        return dslContext.selectFrom(SignalRel.INSTANCE)
                .fetch(record -> Signal.builder()
                        .symbol(record.get(SignalRel.SYMBOL))
                        .direction(record.get(SignalRel.DIRECTION))
                        .triggerDate(record.get(SignalRel.TRIGGER_DATE))
                        .trendUpperBound(record.get(SignalRel.TREND_UPPER_BOUND))
                        .trendLowerBound(record.get(SignalRel.TREND_LOWER_BOUND))
                        .lastClose(record.get(SignalRel.LAST_CLOSE))
                        .build());
    }
}
