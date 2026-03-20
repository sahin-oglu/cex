package com.sahinoglu.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sahinoglu.wallet.Wallet;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Wallet fromWallet;

	@ManyToOne
	private Wallet toWallet;

	private BigDecimal amount;

	private LocalDateTime executedAt;
}
