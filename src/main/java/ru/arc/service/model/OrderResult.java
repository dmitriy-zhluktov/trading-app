package ru.arc.service.model;

import lombok.Builder;

@Builder
final public class OrderResult {
    public final int code;
    public final String msg;
    public final String orderId;
    public final String orderLinkId;
}
