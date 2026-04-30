package com.sahinoglu.coin;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CoinGeckoCoinResponse(String id, String symbol, String name,

		@JsonProperty("current_price") BigDecimal currentPrice,

		@JsonProperty("market_cap") Long marketCap) {
}