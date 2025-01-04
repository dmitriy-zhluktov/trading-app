package ru.arc.config;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.restApi.BybitApiAccountRestClient;
import com.bybit.api.client.restApi.BybitApiTradeRestClient;
import com.bybit.api.client.service.BybitApiClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.arc.config.properties.BybitProperties;

@Configuration
public class RestClientConfig {

    @Bean
    public BybitApiTradeRestClient tradeClient(final BybitProperties bybitProperties) {
        return BybitApiClientFactory.newInstance(
                        bybitProperties.apiKey,
                        bybitProperties.apiSecret,
                        bybitProperties.testNet ? BybitApiConfig.TESTNET_DOMAIN : BybitApiConfig.MAINNET_DOMAIN,
                        bybitProperties.testNet
                )
                .newTradeRestClient();
    }

    @Bean
    public BybitApiAccountRestClient accountClient(final BybitProperties bybitProperties) {
        return BybitApiClientFactory.newInstance(
                        bybitProperties.apiKey,
                        bybitProperties.apiSecret,
                        bybitProperties.testNet ? BybitApiConfig.TESTNET_DOMAIN : BybitApiConfig.MAINNET_DOMAIN,
                        bybitProperties.testNet
                )
                .newAccountRestClient();
    }
}
