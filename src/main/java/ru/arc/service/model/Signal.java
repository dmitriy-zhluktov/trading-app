package ru.arc.service.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public final class Signal {
    public final String symbol;
    public final String direction;
    public final OffsetDateTime triggerDate;
    public final BigDecimal trendUpperBound;
    public final BigDecimal trendLowerBound;
    public final BigDecimal lastClose;
}
