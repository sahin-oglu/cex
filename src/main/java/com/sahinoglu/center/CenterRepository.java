package com.sahinoglu.center;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CenterRepository extends JpaRepository<Center, Long> {

	boolean existsByName(String name);

	List<Center> findByActiveTrue();
}