package ru.arc.service.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
final public class Order {
    private final BigDecimal execPrice;
    private final String side;
}
