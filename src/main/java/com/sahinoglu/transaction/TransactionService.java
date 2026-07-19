package com.sahinoglu.transaction;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.Role;
import com.sahinoglu.exception.ForbiddenException;
import com.sahinoglu.exception.NotFoundException;
import com.sahinoglu.security.ScopeGuard;
import com.sahinoglu.security.SecurityUtils;
import com.sahinoglu.wallet.Wallet;
import com.sahinoglu.wallet.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class TransactionService {

	private final TransactionRepository transactionRepository;
	private final WalletRepository walletRepository;
	private final ScopeGuard scopeGuard;

	public List<TransactionResponse> listTransactionHistory() {

		Employee current = SecurityUtils.getCurrentEmployee();

		List<Transaction> transactions;

		if (current.getRole() == Role.ORG_ADMIN) {
			transactions = transactionRepository.findAll();

		} else if (current.getRole() == Role.CENTER_ADMIN) {

			Long centerId = scopeGuard.requireCurrentCenterId();

			transactions = transactionRepository.findByFromWalletBranchCenterIdOrToWalletBranchCenterId(centerId,
					centerId);

		} else if (current.getRole() == Role.BRANCH_ADMIN) {

			Long branchId = scopeGuard.requireCurrentBranchId();

			transactions = transactionRepository.findByFromWalletBranchIdOrToWalletBranchId(branchId, branchId);

		} else {
			throw new ForbiddenException("Only admins can view transaction history");
		}

		return transactions.stream().map(this::mapToResponse).toList();
	}

	public List<TransactionResponse> listByWallet(Long walletId) {

		Wallet wallet = walletRepository.findById(walletId)
				.orElseThrow(() -> new NotFoundException("Wallet not found"));

		validateWalletScope(wallet);

		List<Transaction> transactions = transactionRepository.findByFromWalletIdOrToWalletId(walletId, walletId);

		return transactions.stream().map(this::mapToResponse).toList();
	}

	private void validateWalletScope(Wallet wallet) {

		Employee current = SecurityUtils.getCurrentEmployee();

		if (current.getRole() == Role.ORG_ADMIN) {
			return;
		}

		if (current.getRole() == Role.CENTER_ADMIN) {

			if (!wallet.getBranch().getCenter().getId().equals(scopeGuard.requireCurrentCenterId())) {
				throw new ForbiddenException("Cannot view transactions for a wallet from another center");
			}

			return;
		}

		if (current.getRole() == Role.BRANCH_ADMIN) {

			if (!wallet.getBranch().getId().equals(scopeGuard.requireCurrentBranchId())) {
				throw new ForbiddenException("Cannot view transactions for a wallet from another branch");
			}

			return;
		}

		throw new ForbiddenException("Only admins can view transaction history");
	}

	private TransactionResponse mapToResponse(Transaction tx) {

		return new TransactionResponse(tx.getId(), tx.getFromWallet().getId(), tx.getToWallet().getId(),
				tx.getFromWallet().getBranch().getId(), tx.getToWallet().getBranch().getId(), tx.getCoin().getId(),
				tx.getCoin().getSymbol(), tx.getAmount(), tx.getPriceAtExecution(), tx.getExecutedAt(),
				tx.getRequest().getId(), tx.getRequestedById(), tx.getRequestedByUsername(), tx.getReviewedById(),
				tx.getReviewedByUsername());
	}

}