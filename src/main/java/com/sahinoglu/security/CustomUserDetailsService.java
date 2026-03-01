//package com.sahinoglu.security;
//
//import com.sahinoglu.employee.Employee;
//import com.sahinoglu.employee.EmployeeRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//
//	private final EmployeeRepository employeeRepository;
//
//	@Override
//	public UserDetails loadUserByUsername(String username) {
//
//		Employee employee = employeeRepository.findByUsername(username)
//				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//		return User.builder().username(employee.getUsername()).password(employee.getPassword())
//				.roles(employee.getRole().name()).build();
//	}
//}