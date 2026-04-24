package com.sahinoglu.transaction;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
/**
 * The naming of the DTO classes within this package may seem funny, but I
 * decided to stick with the standard I had for this project.
 */
public class TransactionRequestRequest {

	@NotNull
	private Long fromWalletId;

	@NotNull
	private Long toWalletId;

	@NotNull
	private String coinId; // "bitcoin"

	@NotNull
	@Positive
	private BigDecimal amount;
}
