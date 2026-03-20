package com.sahinoglu.wallet;

import java.math.BigDecimal;

import com.sahinoglu.branch.Branch;
import com.sahinoglu.customer.Customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "wallets")
@Data
public class Wallet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	private Customer customer;

	@ManyToOne(optional = false)
	private Branch branch;
	// prescision icin bigDecimal deneyecegim ilk defa..
	@Column(nullable = false)
	private BigDecimal balance = BigDecimal.ZERO;

	private boolean active = true;
}