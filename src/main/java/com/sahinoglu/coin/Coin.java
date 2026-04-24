package com.sahinoglu.coin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Coin {

	@Id
	private String id; // "bitcoin", "ethereum"
	@Column(nullable = false)
	private String symbol; // "BTC", "ETH"
	@Column(nullable = false)

	private String name; // "Bitcoin", "Ethereum"
	@Column(nullable = false, precision = 19, scale = 8)

	private BigDecimal price;

	private BigDecimal marketCap; // belki ileride frontend'de coin list sayfasi olursa diye..

	private LocalDateTime lastUpdated; // bu service'de halledilecek..
}