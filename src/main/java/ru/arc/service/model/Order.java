package ru.arc.service.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
final public class Order {
    public final String orderId;
    public final String symbol;
    public final String orderStatus;
    public final String side;
    public final String orderType;
    public final String stopOrderType;
    public final BigDecimal cumExecQty;
    public final BigDecimal avgPrice;
}
