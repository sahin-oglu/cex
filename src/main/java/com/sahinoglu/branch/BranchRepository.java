package com.sahinoglu.branch;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
	Optional<Branch> findByName(String name);

	Optional<Branch> findByLocation(String location);

	List<Branch> findByActiveTrue();

	// non-admin kullanicilar icin
	Optional<Branch> findByIdAndActiveTrue(Long id);

	boolean existsByNameAndCenterId(String name, Long centerId);

	List<Branch> findListByCenterId(Long id);

}
