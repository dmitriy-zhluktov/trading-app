package ru.arc.service.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class WalletBalance {
    private final String totalWalletBalance;
    private final List<CoinBalance> coin;

    @Builder
    @Getter
    public static class CoinBalance {
        private final String coin;
        private final String equity;
        private final String usdValue;
        private final String walletBalance;
        private final String locked;
    }
}
