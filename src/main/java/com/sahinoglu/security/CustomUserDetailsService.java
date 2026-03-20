package com.sahinoglu.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.EmployeeRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final EmployeeRepository employeeRepository;

	public CustomUserDetailsService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Employee employee = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		return new CustomUserDetails(employee);
	}
}