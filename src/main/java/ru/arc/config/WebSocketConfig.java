package ru.arc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import ru.arc.config.properties.WebSocketProperties;
import ru.arc.service.TradeService;
import ru.arc.socket.WsHandler;

@Configuration
public class WebSocketConfig {

    @Bean
    public WsHandler wsHandler(final TradeService tradeService) {
        return new WsHandler(tradeService);
    }

    @Bean
    public WebSocketStompClient webSocketClient(
            final WebSocketProperties webSocketProperties,
            final WsHandler wsHandler
    ) {
        final var client = new StandardWebSocketClient();
        final var stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.connect(webSocketProperties.url, wsHandler);

        return stompClient;
    }
}