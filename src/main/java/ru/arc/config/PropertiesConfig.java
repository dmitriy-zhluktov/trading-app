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

    @Bean
    public TradeProperties tradeProperties(
            @Value("${trade.sellAmountPercent}") final int sellAmountPercent,
            @Value("${trade.priceDiffPercent}") final int priceDiffPercent,
            @Value("${trade.buyAmountUsdt}") final BigDecimal buyAmountUsdt
    ) {
        return TradeProperties.builder()
                .sellAmountPercent(sellAmountPercent)
                .priceDiffPercent(priceDiffPercent)
                .buyAmountUsdt(buyAmountUsdt)
                .build();
    }
}
