package com.sahinoglu.wallet.asset;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class WalletAssetOperationRequest {

	@NotNull
	@Positive
	private BigDecimal amount;
}