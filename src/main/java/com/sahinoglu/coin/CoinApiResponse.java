package com.sahinoglu.coin;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class CoinApiResponse {

	private String id;
	private String symbol;
	private String name;
	private BigDecimal price;
	private BigDecimal marketCap;
}
