package com.sahinoglu.branch;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
	Optional<Branch> findByName(String name);

	Optional<Branch> findByLocation(String location);

	List<Branch> findByActiveTrue();

	Optional<Branch> findByIdAndActiveTrue(Long id);

	boolean existsByNameAndCenterId(String name, Long centerId);

	List<Branch> findListByCenterId(Long id);

	@Modifying
	@Query("""
			    update Branch b
			    set b.active = false
			    where b.center.id = :centerId
			      and b.active = true
			""")
	void deactivateBranchesByCenterId(@Param("centerId") Long centerId);
}
