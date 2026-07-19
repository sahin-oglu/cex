package com.sahinoglu.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * The naming of the DTO classes within this package may seem funny, but I
 * decided to stick with the standard I had for this project.
 */
public record TransactionRequestResponse(Long id, BigDecimal amount, Long fromWalletId, Long toWalletId,
		String coinId, String coinSymbol, TransactionRequestStatus status, Long requestedById, Long reviewedById,
		LocalDateTime requestedAt, LocalDateTime reviewedAt) {
}
