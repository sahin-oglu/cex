package com.sahinoglu.transaction;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;

	@GetMapping("/api/v1/admin/transactions")
	public List<TransactionResponse> listTransactionHistory() {
		return transactionService.listTransactionHistory();
	}

	@GetMapping("/api/v1/wallets/{walletId}/transactions")
	public List<TransactionResponse> listByWallet(@PathVariable Long walletId) {

		return transactionService.listByWallet(walletId);
	}

}
