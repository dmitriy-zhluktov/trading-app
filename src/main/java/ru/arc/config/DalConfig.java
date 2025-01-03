package ru.arc.config;

import com.bybit.api.client.restApi.BybitApiAccountRestClient;
import com.bybit.api.client.restApi.BybitApiAsyncTradeRestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.arc.dal.TradeDal;
import ru.arc.dal.impl.TradeDalImpl;

@Configuration
public class DalConfig {

    @Bean
    public TradeDal tradeDal(
            final BybitApiAsyncTradeRestClient tradeClient,
            final BybitApiAccountRestClient accountClient,
            final ObjectMapper objectMapper
            ) {
        return new TradeDalImpl(
                tradeClient,
                accountClient,
                objectMapper
        );
    }
}
