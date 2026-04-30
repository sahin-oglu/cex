package com.sahinoglu.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sahinoglu.coin.Coin;
import com.sahinoglu.wallet.Wallet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "transactions")
@Data
/**
 * transactions are immutable in my project.
 * 
 * 
 * 
 * transactions and transactionRequests are separate entities with separate
 * repositories.
 */
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	private Wallet fromWallet;
	@ManyToOne(optional = false)
	private Wallet toWallet;

	@ManyToOne(optional = false)
	private Coin coin;
	@Column(precision = 19, scale = 8)
	private BigDecimal priceAtExecution;

	private BigDecimal amount;
	private LocalDateTime executedAt;
	@OneToOne
	private TransactionRequest request;
	
	
	private Long requestedById;
	private String requestedByUsername;

	private Long reviewedById;
	private String reviewedByUsername;

	

}
