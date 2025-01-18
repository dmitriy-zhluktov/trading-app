package ru.arc.config;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.restApi.BybitApiAccountRestClient;
import com.bybit.api.client.restApi.BybitApiMarketRestClient;
import com.bybit.api.client.restApi.BybitApiTradeRestClient;
import com.bybit.api.client.service.BybitApiClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.arc.config.properties.BybitProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RestClientConfig {

    private static final Map<String, String> tradingNets = new HashMap<>();
    static {
        tradingNets.put("test", BybitApiConfig.TESTNET_DOMAIN);
        tradingNets.put("demo", BybitApiConfig.DEMO_TRADING_DOMAIN);
        tradingNets.put("main", BybitApiConfig.MAINNET_DOMAIN);
    }

    @Bean
    public BybitApiTradeRestClient tradeClient(final BybitProperties bybitProperties) {
        final var net = tradingNets.get(bybitProperties.tradingNet.toLowerCase());
        return BybitApiClientFactory.newInstance(
                        bybitProperties.apiKey,
                        bybitProperties.apiSecret,
                        net,
                        !net.equals(BybitApiConfig.MAINNET_DOMAIN)
                )
                .newTradeRestClient();
    }

    @Bean
    public BybitApiAccountRestClient accountClient(final BybitProperties bybitProperties) {
        final var net = tradingNets.get(bybitProperties.tradingNet.toLowerCase());
        return BybitApiClientFactory.newInstance(
                        bybitProperties.apiKey,
                        bybitProperties.apiSecret,
                        net,
                        !net.equals(BybitApiConfig.MAINNET_DOMAIN)
                )
                .newAccountRestClient();
    }

    @Bean
    public BybitApiMarketRestClient marketClient(final BybitProperties bybitProperties) {
        final var net = tradingNets.get(bybitProperties.tradingNet.toLowerCase());
        return BybitApiClientFactory.newInstance(
                        bybitProperties.apiKey,
                        bybitProperties.apiSecret,
                        net,
                        !net.equals(BybitApiConfig.MAINNET_DOMAIN)
                )
                .newMarketDataRestClient();
    }
}
