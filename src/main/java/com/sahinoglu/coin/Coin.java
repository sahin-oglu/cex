package com.sahinoglu.coin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "coins")
@Getter
@Setter
@NoArgsConstructor

public class Coin {

	@Id
	private String id; // "bitcoin", "ethereum"
	
    @Column(nullable = false)
	private String symbol; // "btc", "eth"
    
    @Column(nullable = false)
	private String name; // "Bitcoin", "Ethereum"

	@Column(nullable = false, precision = 19, scale = 8)
	private BigDecimal price;
	private Long marketCap;

    @Column(nullable = false)
	private LocalDateTime lastUpdated; 
}