package com.sahinoglu.coin;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CoinService {

	private final CoinRepository coinRepository;
	private final RestClient restClient = RestClient.create("https://api.coingecko.com/api/v3");

	@Transactional
	public void syncCoins() {
		List<CoinGeckoCoinResponse> apiCoins = restClient.get()
				.uri("/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=100&page=1&sparkline=false")
				.retrieve().body(new ParameterizedTypeReference<>() {
				});

		LocalDateTime now = LocalDateTime.now();

		for (CoinGeckoCoinResponse apiCoin : apiCoins) {
			Coin coin = coinRepository.findById(apiCoin.id()).orElseGet(Coin::new);

			coin.setId(apiCoin.id());
			coin.setSymbol(apiCoin.symbol().toUpperCase());
			coin.setName(apiCoin.name());
			coin.setPrice(apiCoin.currentPrice());
			coin.setMarketCap(apiCoin.marketCap());
			coin.setLastUpdated(now);

			coinRepository.save(coin);
		}
	}

	public List<Coin> list() {
		return coinRepository.findAll();
	}
}