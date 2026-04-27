package com.sahinoglu.coin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CoinController {

	private final CoinService coinService;

	@PostMapping("/admin/coins/sync")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void syncCoins() {
		coinService.syncCoins();
	}

	@GetMapping("/coins")
	public List<Coin> listCoins() {
		return coinService.listCoins();
	}
}