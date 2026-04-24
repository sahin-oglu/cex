package com.sahinoglu.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sahinoglu.coin.Coin;
import com.sahinoglu.employee.Employee;
import com.sahinoglu.wallet.Wallet;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class TransactionRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(optional = false)
	private Coin coin;
	private BigDecimal amount;

	@ManyToOne(optional = false)
	private Wallet fromWallet;

	@ManyToOne(optional = false)
	private Wallet toWallet;

	@ManyToOne(optional = false)
	private Employee requestedBy;

	private LocalDateTime requestedAt;

	@ManyToOne
	private Employee reviewedBy;

	private LocalDateTime reviewedAt;

	@Enumerated(EnumType.STRING)
	private TransactionRequestStatus status; // PENDING, APPROVED, REJECTED
}
