package ru.arc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.arc.config.properties.BybitProperties;
import ru.arc.config.properties.TradeProperties;
import ru.arc.config.properties.WebSocketProperties;

import java.math.BigDecimal;

@Configuration
public class PropertiesConfig {

    @Bean
    public BybitProperties bybitProperties(
            @Value("${bybit.apiKey}") final String apiKey,
            @Value("${bybit.apiSecret}") final String apiSecret,
            @Value("${bybit.tradingNet:main}") final String tradingNet
    ) {
        return BybitProperties.builder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .tradingNet(tradingNet)
                .build();
    }

    @Bean
    public WebSocketProperties webSocketProperties(
            @Value("${websocket.url}") final String url,
            @Value("${websocket.topic}") final String topic
    ) {
        return WebSocketProperties.builder()
                .url(url)
                .topic(topic)
                .build();
    }

    @Bean
    public TradeProperties tradeProperties(
            @Value("${trade.sellAmountPercent}") final BigDecimal sellAmountPercent,
            @Value("${trade.priceDiffPercent}") final BigDecimal priceDiffPercent,
            @Value("${trade.buyAmountUsdt}") final BigDecimal buyAmountUsdt
    ) {
        return TradeProperties.builder()
                .sellAmountPercent(sellAmountPercent)
                .priceDiffPercent(priceDiffPercent)
                .buyAmountUsdt(buyAmountUsdt)
                .build();
    }
}
