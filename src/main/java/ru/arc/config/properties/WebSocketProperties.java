package ru.arc.config.properties;

import lombok.Builder;

@Builder
public class WebSocketProperties {
    public final String url;
    public final String topic;
}
