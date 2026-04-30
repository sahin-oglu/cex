package com.sahinoglu.coin;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CoinSyncScheduler {

	private final CoinService coinService;

	// her saat basi

	@Scheduled(cron = "0 0 * * * *")
	public void syncCoinsHourly() {
	    System.out.println("Coin sync started..."+ new Date());
		coinService.syncCoins();
	}
}