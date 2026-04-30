package com.sahinoglu.coin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table
public class Coin {

	@Id
	private String id; // "bitcoin", "ethereum"
	private String symbol; // "btc", "eth"
	private String name; // "Bitcoin", "Ethereum"

	@Column(nullable = false, precision = 19, scale = 8)
	private BigDecimal price;
	private Long marketCap;

	private LocalDateTime lastUpdated; // bu service'de halledilecek..
}