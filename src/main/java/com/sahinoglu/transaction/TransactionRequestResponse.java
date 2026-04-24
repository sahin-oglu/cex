package com.sahinoglu.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

/**
 * The naming of the DTO classes within this package may seem funny, but I
 * decided to stick with the standard I had for this project.
 */

public class TransactionRequestResponse {

    private Long id;

    private BigDecimal amount;

    private Long fromWalletId;
    private Long toWalletId;

    private String coinId;
    private String coinSymbol;

    private TransactionRequestStatus status;

    private Long requestedById;
    private Long reviewedById;

    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
}