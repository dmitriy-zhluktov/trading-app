package ru.arc.socket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Builder
@Jacksonized
public class CurrencyUpdate {
    public final String symbol;
    public final String direction;
    @JsonProperty("trend_upper_bound")
    public final BigDecimal trendUpperBound;
    @JsonProperty("trend_lower_bound")
    public final BigDecimal trendLowerBound;
    @JsonProperty("last_close")
    public final BigDecimal lastClose;
}


