package ru.arc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.arc.config.properties.TradeProperties;
import ru.arc.dal.TradeDal;
import ru.arc.service.TradeService;
import ru.arc.service.impl.TradeServiceImpl;

@Configuration
public class ServiceConfig {

    @Bean
    public TradeService tradeService(
            final TradeDal tradeDal,
            final TradeProperties tradeProperties
    ) {
        return new TradeServiceImpl(tradeDal, tradeProperties);
    }
}
