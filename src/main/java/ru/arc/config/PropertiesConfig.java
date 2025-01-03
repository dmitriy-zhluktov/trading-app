package ru.arc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.arc.config.properties.BybitProperties;
import ru.arc.config.properties.WebSocketProperties;

@Configuration
public class PropertiesConfig {

    @Bean
    public BybitProperties bybitProperties(
            @Value("${bybit.apiKey}") final String apiKey,
            @Value("${bybit.apiSecret}") final String apiSecret,
            @Value("${bybit.testNet:false}") final boolean testNet
    ) {
        return BybitProperties.builder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .testNet(testNet)
                .build();
    }

    @Bean
    public WebSocketProperties webSocketProperties(
            @Value("${websocket.url}") final String url
    ) {
        return WebSocketProperties.builder()
                .url(url)
                .build();
    }
}
