package com.sahinoglu.coin;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CoinApiClient {

	public List<CoinApiResponse> getCoins() {
		return List.of(
				//bunu degistirecem ileride, service icinde api pull yapicam.!
				new CoinApiResponse("bitcoin", "BTC", "Bitcoin", new BigDecimal("65000"),
						new BigDecimal("1200000000000")),
				new CoinApiResponse("ethereum", "ETH", "Ethereum", new BigDecimal("3000"),
						new BigDecimal("400000000000"))
				
				
				);
	}
}