package ru.arc.service.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
final public class CoinTicker {
    public final BigDecimal lastPrice;
    public final String symbol;
}
