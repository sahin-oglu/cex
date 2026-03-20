package com.sahinoglu.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sahinoglu.employee.Employee;
import com.sahinoglu.wallet.Wallet;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "transaction_requests")
@Data
public class TransactionRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Wallet fromWallet;

	@ManyToOne
	private Wallet toWallet;

	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	private RequestStatus status;

	@ManyToOne
	private Employee requestedBy;

	@ManyToOne
	private Employee reviewedBy;

	private LocalDateTime requestedAt;

	private LocalDateTime reviewedAt;
}
