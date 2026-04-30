package com.sahinoglu.wallet.asset;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WalletAssetConversionResponse {

	private Long walletId;

	private String fromCoinId;
	private String fromCoinSymbol;

	private String toCoinId;
	private String toCoinSymbol;

	private BigDecimal spentAmount;
	private BigDecimal feeAmount;
	private BigDecimal netSpentAmount;

	private BigDecimal receivedAmount;
	private BigDecimal targetAssetBalanceAfterConversion;
}