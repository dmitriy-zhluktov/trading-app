package ru.arc.service.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public final class WalletBalance {
    public final String totalWalletBalance;
    public final List<CoinBalance> coin;

    @Builder
    public static final class CoinBalance {
        public final String coin;
        public final String equity;
        public final String usdValue;
        public final BigDecimal walletBalance;
        public final BigDecimal locked;
    }
}
