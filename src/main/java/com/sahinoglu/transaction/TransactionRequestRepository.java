package com.sahinoglu.transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRequestRepository extends JpaRepository<TransactionRequest, Long> {
	List<TransactionRepository> findByStatus(TransactionRequestStatus status);

	List<TransactionRequest> findByRequestedById(Long employeeId);

	List<TransactionRequest> findByFromWalletBranchId(Long branchId);

	List<TransactionRequest> findByFromWalletBranchCenterId(Long centerId);
}
