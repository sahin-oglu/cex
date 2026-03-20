package com.sahinoglu.employee;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	Optional<Employee> findByUsername(String username);

	List<Employee> findByCenterId(Long centerId);

	List<Employee> findByBranchId(Long branchId);

	boolean existsByUsername(String username);
}
