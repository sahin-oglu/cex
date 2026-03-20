package com.sahinoglu.wallet;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

	List<Wallet> findByActiveTrue();

	Optional<Wallet> findByIdAndActiveTrue(Long id);

	List<Wallet> findByCustomerId(Long customerId);

	List<Wallet> findByBranchId(Long branchId);

	List<Wallet> findByBranchCenterId(Long centerId);

}
