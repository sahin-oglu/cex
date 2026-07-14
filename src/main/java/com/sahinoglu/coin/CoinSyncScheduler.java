package com.sahinoglu.coin;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CoinSyncScheduler {

	private final CoinService coinService;

	// every hourmark

	@Scheduled(cron = "0 0 * * * *")
	public void syncCoinsHourly() {
		System.out.println("Coin sync started..." + new Date());
		coinService.syncCoins();
	}
}