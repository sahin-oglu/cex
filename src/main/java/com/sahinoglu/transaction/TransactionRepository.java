package com.sahinoglu.transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	// su anlik gerek yok bunlara.
//	List<Transaction> findByFromWalletId(Long fromWalletId);
//	List<Transaction> findByToWalletId(Long toWalletId);
//	List<Transaction> findByFromWalletIdOrToWalletId(Long fromWalletId, Long toWalletId);

	// öhh
	List<Transaction> findByFromWalletBranchIdOrToWalletBranchId(Long fromBranchId, Long toBranchId);

	List<Transaction> findByFromWalletBranchCenterIdOrToWalletBranchCenterId(Long fromCenterId, Long toCenterId);

}
