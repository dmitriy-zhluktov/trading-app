package ru.arc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import ru.arc.config.properties.WebSocketProperties;
import ru.arc.dao.SignalDao;
import ru.arc.service.TradeService;
import ru.arc.socket.FlatWsMessageHandler;
import ru.arc.socket.ReconnectingWebSocketConnectionManager;

@Configuration
public class WebSocketConfig {

    @Bean
    public FlatWsMessageHandler flatWsMessageHandler(
            final TradeService tradeService,
            final ObjectMapper objectMapper,
            SignalDao signalDao
            ) {
        return new FlatWsMessageHandler(tradeService, objectMapper, signalDao);
    }

    @Bean
    public WebSocketConnectionManager wsConnectionManager(
            final WebSocketProperties webSocketProperties,
            final FlatWsMessageHandler flatWsMessageHandler
    ) {

        WebSocketConnectionManager manager = new ReconnectingWebSocketConnectionManager(
                new StandardWebSocketClient(),
                flatWsMessageHandler,
                webSocketProperties.url
        );
        manager.setAutoStartup(true);

        return manager;
    }
}