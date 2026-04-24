package com.sahinoglu.coin;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CoinService {

	private final CoinRepository coinRepository;
	private final CoinApiClient coinApiClient;

	public CoinService(CoinRepository coinRepository, CoinApiClient coinApiClient) {
		this.coinRepository = coinRepository;
		this.coinApiClient = coinApiClient;
	}

	public void syncCoins() {

		List<CoinApiResponse> apiCoins = coinApiClient.getCoins();
		LocalDateTime now = LocalDateTime.now();// loop disinda olmasi daha dogru
		for (CoinApiResponse c : apiCoins) {

			Coin coin = coinRepository.findById(c.getId()).orElse(new Coin());

			coin.setId(c.getId());
			coin.setSymbol(c.getSymbol());
			coin.setName(c.getName());
			coin.setPrice(c.getPrice());
			coin.setMarketCap(c.getMarketCap());
			coin.setLastUpdated(now);

			coinRepository.save(coin);
		}
	}
}