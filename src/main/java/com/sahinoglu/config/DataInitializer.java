package com.sahinoglu.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.sahinoglu.branch.Branch;
import com.sahinoglu.center.Center;
import com.sahinoglu.center.CenterRepository;
import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.EmployeeRepository;
import com.sahinoglu.employee.Role;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final EmployeeRepository employeeRepository;
	private final CenterRepository centerRepository;

	@Override
	public void run(String... args) {

		if (employeeRepository.findByUsername("admin").isPresent()) {
			return;
		}
//		Center center = new Center();
//		center.setLocation("headquarters");
//		center.setName("headquarters");
//		centerRepository.save(center);
		Employee admin = new Employee();
		admin.setUsername("admin");
		admin.setPassword("admin");
		admin.setFirstName("System");
		admin.setLastName("Admin");
		admin.setRole(Role.ORG_ADMIN);
//		admin.setCenter(center);
		
		employeeRepository.save(admin);
	}
}