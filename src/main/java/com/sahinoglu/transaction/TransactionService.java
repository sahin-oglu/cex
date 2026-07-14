package com.sahinoglu.transaction;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.Role;
import com.sahinoglu.exception.ForbiddenException;
import com.sahinoglu.exception.NotFoundException;
import com.sahinoglu.security.SecurityUtils;
import com.sahinoglu.wallet.Wallet;
import com.sahinoglu.wallet.WalletRepository;

import lombok.RequiredArgsConstructor;

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

			Long centerId = SecurityUtils.getCurrentCenterId();

			if (centerId == null) {
				throw new ForbiddenException("Current user is not assigned to a center");
			}

			if (!wallet.getBranch().getCenter().getId().equals(centerId)) {
				throw new ForbiddenException("Cannot view transactions for a wallet from another center");
			}

			return;
		}

		if (current.getRole() == Role.BRANCH_ADMIN) {

			Long branchId = SecurityUtils.getCurrentBranchId();

			if (branchId == null) {
				throw new ForbiddenException("Current user is not assigned to a branch");
			}

			if (!wallet.getBranch().getId().equals(branchId)) {
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