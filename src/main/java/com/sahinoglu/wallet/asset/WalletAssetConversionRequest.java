package com.sahinoglu.wallet.asset;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class WalletAssetConversionRequest {

	@NotBlank
	private String fromCoinId;

	@NotBlank
	private String toCoinId;

	@NotNull
	@Positive
	private BigDecimal amount;
}