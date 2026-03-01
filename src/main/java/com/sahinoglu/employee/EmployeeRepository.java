package com.sahinoglu.employee;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
	Optional<Employee> findByUsername(String username);
}
