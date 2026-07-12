package com.sahinoglu.wallet.asset;

import java.math.BigDecimal;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor

@Table(name = "wallet_assets", uniqueConstraints = { @UniqueConstraint(columnNames = { "wallet_id", "coin_id" }) })
public class WalletAsset {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "wallet_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Wallet wallet;

	@JoinColumn(name = "coin_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Coin coin;
    @Column(nullable = false, precision = 19, scale = 8)
	private BigDecimal amount;
}
