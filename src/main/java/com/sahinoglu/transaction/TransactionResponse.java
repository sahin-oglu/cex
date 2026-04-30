package com.sahinoglu.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionResponse {

	private Long id;

	private Long fromWalletId;
	private Long toWalletId;

	private Long fromBranchId;
	private Long toBranchId;

	private String coinId;
	private String coinSymbol;

	private BigDecimal amount;
	private BigDecimal priceAtExecution;

	private LocalDateTime executedAt;
	private Long requestId;

	private Long requestedById;
	private String requestedByUsername;

	private Long reviewedById;
	private String reviewedByUsername;

}
