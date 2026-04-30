package com.sahinoglu.transaction;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.Role;
import com.sahinoglu.exception.ForbiddenException;
import com.sahinoglu.security.SecurityUtils;
import com.sahinoglu.wallet.WalletRepository;

import lombok.RequiredArgsConstructor;

/**
 * use case: branchAdmin kendi branch'ine ait transaction'lari gorebilir, //
 * centerAdmin kendi center'ina ait branch'lere ait branch'lerderdeki //
 * transaction'lari gorebilir.
 */
@Service
@RequiredArgsConstructor

public class TransactionService {

	private final TransactionRepository transactionRepository;
	private final WalletRepository walletRepository;

	public List<TransactionResponse> listTransactionHistory() {

		Employee current = SecurityUtils.getCurrentEmployee();

		List<Transaction> transactions;

		if (current.getRole() == Role.ORG_ADMIN) {
			transactions = transactionRepository.findAll();

		} else if (current.getRole() == Role.CENTER_ADMIN) {

			Long centerId = SecurityUtils.getCurrentCenterId();

			if (centerId == null) {
				throw new ForbiddenException("Current user is not assigned to a center");
			}

			transactions = transactionRepository.findByFromWalletBranchCenterIdOrToWalletBranchCenterId(centerId,
					centerId);

		} else if (current.getRole() == Role.BRANCH_ADMIN) {

			Long branchId = SecurityUtils.getCurrentBranchId();

			if (branchId == null) {
				throw new ForbiddenException("Current user is not assigned to a branch");
			}

			transactions = transactionRepository.findByFromWalletBranchIdOrToWalletBranchId(branchId, branchId);

		} else {
			throw new ForbiddenException("Only admins can view transaction history");
		}

		return transactions.stream().map(this::mapToResponse).toList();
	}

	private TransactionResponse mapToResponse(Transaction tx) {

		return new TransactionResponse(tx.getId(), tx.getFromWallet().getId(), tx.getToWallet().getId(),
				tx.getFromWallet().getBranch().getId(), tx.getToWallet().getBranch().getId(), tx.getCoin().getId(),
				tx.getCoin().getSymbol(), tx.getAmount(), tx.getPriceAtExecution(), tx.getExecutedAt(),
				tx.getRequest().getId(), tx.getRequestedById(), tx.getRequestedByUsername(), tx.getReviewedById(),
				tx.getReviewedByUsername());
	}
}