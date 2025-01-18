package ru.arc.service.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
final public class Order {
    public final BigDecimal execPrice;
    public final String side;
}
