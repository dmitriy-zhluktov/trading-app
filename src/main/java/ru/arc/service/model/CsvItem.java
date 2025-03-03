package ru.arc.service.model;

import lombok.Builder;

@Builder
public class CsvItem {
    public final String symbol;
    public final String buyPrice;
    public final String buyValue;
    public final String sellPrice;
    public final String sellValue;
    public final String buyDate;
    public final String sellDate;
}
