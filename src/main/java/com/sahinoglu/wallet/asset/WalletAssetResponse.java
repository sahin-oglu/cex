package com.sahinoglu.wallet.asset;

import java.math.BigDecimal;

public record WalletAssetResponse(Long walletId, String coinId, String coinSymbol, String coinName,
		BigDecimal amount) {
}
