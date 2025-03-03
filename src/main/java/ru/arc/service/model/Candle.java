package ru.arc.service.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public final class Candle {
    public final String symbol;
    public final Long dateTime;
    public final BigDecimal openPrice;
    public final BigDecimal highPrice;
    public final BigDecimal lowPrice;
    public final BigDecimal closePrice;
}
