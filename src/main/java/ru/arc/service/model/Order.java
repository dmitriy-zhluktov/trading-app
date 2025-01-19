package ru.arc.service.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
final public class Order {
    public final String orderStatus;
    public final String side;
    public final BigDecimal cumExecQty;
    public final BigDecimal avgPrice;
}
