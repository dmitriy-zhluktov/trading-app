package ru.arc.service.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
final public class SpotCoinInstruments {
    public final String symbol;
    public final BigDecimal basePrecision; // precision for coin qty.
    public final BigDecimal quotePrecision; // precision for quote qty
    public final BigDecimal minOrderQty; // min qty coin
    public final BigDecimal maxOrderQty; // max qty coin
    public final BigDecimal minOrderAmt; // min qty quote
    public final BigDecimal maxOrderAmt; // max qty quote
    public final BigDecimal tickSize; // precision for quote value (price)
}
