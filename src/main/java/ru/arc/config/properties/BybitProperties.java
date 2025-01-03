package ru.arc.config.properties;

import lombok.Builder;

@Builder
public final class BybitProperties {
    public final String apiKey;
    public final String apiSecret;
    public final boolean testNet;
}
