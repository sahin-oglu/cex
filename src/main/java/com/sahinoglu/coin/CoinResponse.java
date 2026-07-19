package com.sahinoglu.coin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CoinResponse(String id, String symbol, String name, BigDecimal price, Long marketCap,
		LocalDateTime lastUpdated) {
}
