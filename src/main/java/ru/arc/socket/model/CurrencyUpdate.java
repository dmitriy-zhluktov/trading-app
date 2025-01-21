package ru.arc.socket.model;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public class CurrencyUpdate {
    public final String symbol;
    public final String direction;
}


