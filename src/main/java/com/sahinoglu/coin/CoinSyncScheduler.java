package com.sahinoglu.coin;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoinSyncScheduler {

	private final CoinService coinService;

	@Scheduled(cron = "0 0 * * * *")
	public void syncCoinsHourly() {
		log.info("Coin sync started");
		coinService.syncCoins();
	}
}