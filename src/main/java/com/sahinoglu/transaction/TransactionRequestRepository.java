package com.sahinoglu.transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRequestRepository extends JpaRepository<TransactionRequest, Long> {
	List<TransactionRepository> findByStatus(TransactionRequestStatus status);

}
