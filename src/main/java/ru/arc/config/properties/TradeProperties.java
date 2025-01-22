package ru.arc.config.properties;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
final public class TradeProperties {
    public final BigDecimal sellAmountPercent;
    public final BigDecimal priceDiffPercent;
    public final BigDecimal buyAmountUsdt;
}
