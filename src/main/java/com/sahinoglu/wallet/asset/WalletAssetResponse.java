package com.sahinoglu.wallet.asset;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WalletAssetResponse {

	private Long walletId;

	private String coinId;
	private String coinSymbol;
	private String coinName;

	private BigDecimal amount;
}