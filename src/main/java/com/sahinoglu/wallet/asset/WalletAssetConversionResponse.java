package com.sahinoglu.wallet.asset;

import java.math.BigDecimal;

public record WalletAssetConversionResponse(Long walletId, String fromCoinId, String fromCoinSymbol, String toCoinId,
		String toCoinSymbol, BigDecimal spentAmount, BigDecimal feeAmount, BigDecimal netSpentAmount,
		BigDecimal receivedAmount, BigDecimal targetAssetBalanceAfterConversion) {
}
