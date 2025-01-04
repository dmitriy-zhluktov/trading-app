package ru.arc.config.properties;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
final public class TradeProperties {
    public final int sellAmountPercent;
    public final int priceDiffPercent;
    public final BigDecimal buyAmountUsdt;
}
