package com.sahinoglu.transaction;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/transactions")
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;

	@GetMapping
	public List<TransactionResponse> listTransactionHistory() {
		return transactionService.listTransactionHistory();
	}

	// lazim olmadigina kanaat getirildi.
//    @GetMapping("/{walletId}")
//    public List<TransactionResponse> listByWallet(@PathVariable Long walletId) {
//        return transactionService.listByWallet(walletId);
//    }

}
