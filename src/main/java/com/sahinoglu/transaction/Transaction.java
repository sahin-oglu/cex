package com.sahinoglu.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sahinoglu.coin.Coin;
import com.sahinoglu.wallet.Wallet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Transactions and Transaction Requests are two separate entities in this
 * project.
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor

public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "from_wallet_id", nullable = false)
	private Wallet fromWallet;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "to_wallet_id", nullable = false)
	private Wallet toWallet;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "coin_id", nullable = false)
	private Coin coin;

	@Column(nullable = false, precision = 19, scale = 8)
	private BigDecimal priceAtExecution;

	@Column(nullable = false, precision = 19, scale = 8)
	private BigDecimal amount;

	@Column(nullable = false)
	private LocalDateTime executedAt;

	//

	//

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "request_id", nullable = false, unique = true)
	private TransactionRequest request;

	@Column(nullable = false)
	private Long requestedById;
	@Column(nullable = false)
	private String requestedByUsername;
	@Column(nullable = false)
	private Long reviewedById;
	@Column(nullable = false)
	private String reviewedByUsername;

}
