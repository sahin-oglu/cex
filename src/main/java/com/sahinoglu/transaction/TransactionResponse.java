package com.sahinoglu.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(Long id, Long fromWalletId, Long toWalletId, Long fromBranchId, Long toBranchId,
		String coinId, String coinSymbol, BigDecimal amount, BigDecimal priceAtExecution, LocalDateTime executedAt,
		Long requestId, Long requestedById, String requestedByUsername, Long reviewedById,
		String reviewedByUsername) {
}
