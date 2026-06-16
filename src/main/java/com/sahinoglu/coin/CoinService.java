package com.sahinoglu.coin;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import jakarta.transaction.Transactional;

@Service
public class CoinService {

    private final CoinRepository coinRepository;
    private final RestClient restClient;

    private final String vsCurrency;
    private final int perPage;

    public CoinService(
            CoinRepository coinRepository,
            @Value("${coingecko.base-url}") String baseUrl,
            @Value("${coingecko.vs-currency}") String vsCurrency,
            @Value("${coingecko.per-page}") int perPage
    ) {
        this.coinRepository = coinRepository;
        this.restClient = RestClient.create(baseUrl);
        this.vsCurrency = vsCurrency;
        this.perPage = perPage;
    }

    @Transactional
    public void syncCoins() {
        List<CoinGeckoCoinResponse> apiCoins = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/coins/markets")
                        .queryParam("vs_currency", vsCurrency)
                        .queryParam("order", "market_cap_desc")
                        .queryParam("per_page", perPage)
                        .queryParam("page", 1)
                        .queryParam("sparkline", false)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
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