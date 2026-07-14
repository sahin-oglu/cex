package com.sahinoglu.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sahinoglu.coin.Coin;
import com.sahinoglu.employee.Employee;
import com.sahinoglu.wallet.Wallet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This is not a DTO class. This name was used for the sake of keeping the
 * naming consistency throughout the project.
 * <p>
 * A Transaction Request is a separate entity that has its own DTO objects.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "transaction_requests")
public class TransactionRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "coin_id", nullable = false)
	private Coin coin;

	@Column(nullable = false, precision = 19, scale = 8)
	private BigDecimal amount;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "from_wallet_id", nullable = false)
	private Wallet fromWallet;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "to_wallet_id", nullable = false)
	private Wallet toWallet;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "requested_by_id", nullable = false)
	private Employee requestedBy;

	@Column(nullable = false)
	private LocalDateTime requestedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reviewed_by_id")
	private Employee reviewedBy;

	private LocalDateTime reviewedAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransactionRequestStatus status;
}