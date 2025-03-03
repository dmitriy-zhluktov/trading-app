package ru.arc.service.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
final public class TradeHistory {
    public final BigDecimal execPrice;
    public final BigDecimal execValue;
    public final String side;
    public final String symbol;
    public final LocalDateTime execTime;
}
