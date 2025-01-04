package ru.arc.socket.model;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Builder
@Getter
@Jacksonized
public class CurrencyUpdate {
    private final String coin;
    private final String direction;
    private final BigDecimal currentPrice;
}


