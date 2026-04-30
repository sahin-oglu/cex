package com.sahinoglu.transaction;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/transaction-requests")
@RequiredArgsConstructor
public class TransactionRequestController {

	private final TransactionRequestService service;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TransactionRequestResponse create(@Valid @RequestBody TransactionRequestRequest request) {
		return service.createTransactionRequest(request);
	}

	@PatchMapping("/{id}/approve")
	public TransactionRequestResponse approve(@PathVariable Long id) {
		return service.approveTransactionRequest(id);
	}

	@PatchMapping("/{id}/reject")
	public TransactionRequestResponse reject(@PathVariable Long id) {
		return service.rejectTransactionRequest(id);
	}

	@GetMapping("/transaction-requests")
	public List<TransactionRequestResponse> list() {
		return service.list();
	}

}