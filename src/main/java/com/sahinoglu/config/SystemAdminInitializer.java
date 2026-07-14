package com.sahinoglu.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.EmployeeRepository;
import com.sahinoglu.employee.Role;

import lombok.RequiredArgsConstructor;

/**
 * ORG_ADMIN is the system invariant for this project. An ORG_ADMIN global
 * user will always be present.
 * 
 */
@Component
@RequiredArgsConstructor
public class SystemAdminInitializer implements CommandLineRunner {

	public static final String ADMIN_USERNAME = "admin";
	public static final String ADMIN_PASSWORD = "admin";

	private final EmployeeRepository employeeRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void run(String... args) {

		if (employeeRepository.findByUsername(ADMIN_USERNAME).isPresent()) {
			return;
		}

		Employee admin = new Employee();
		admin.setUsername(ADMIN_USERNAME);
		admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
		admin.setFirstName("System");
		admin.setLastName("Admin");
		admin.setRole(Role.ORG_ADMIN);

		employeeRepository.save(admin);
	}
}
