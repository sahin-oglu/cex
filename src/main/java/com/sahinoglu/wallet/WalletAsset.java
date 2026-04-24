package com.sahinoglu.wallet;

import java.math.BigDecimal;

import com.sahinoglu.coin.Coin;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "wallet_id", "coin_id" }) })
public class WalletAsset {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "wallet_id")
	@ManyToOne(optional = false)
	private Wallet wallet;

	@JoinColumn(name = "coin_id")
	@ManyToOne(optional = false)
	private Coin coin;

	private BigDecimal amount;
}
