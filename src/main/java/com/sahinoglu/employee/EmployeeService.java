package com.sahinoglu.employee;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository repository;

	public CreateEmployeeResponse createEmployee(CreateEmployeeRequest request) {

		if (repository.findByUsername(request.getUsername()).isPresent()) {
			throw new RuntimeException("Username already exists");
		}

		Employee employee = new Employee();
		employee.setUsername(request.getUsername());
		employee.setPassword(request.getPassword());
		employee.setFirstName(request.getFirstName());
		employee.setLastName(request.getLastName());
		employee.setRole(request.getRole());
		Employee saved = repository.save(employee);
		
		return new CreateEmployeeResponse(saved.getId(), saved.getUsername(), saved.getFirstName(), saved.getLastName(),
				saved.getRole());
	}

	public List<CreateEmployeeRequest> getEmployeeList() {
		List<Employee> employeeList = repository.findAll();
		List<CreateEmployeeRequest> response = new ArrayList<>();

		for (Employee employee : employeeList) {
			CreateEmployeeRequest dtoEmployee = new CreateEmployeeRequest();
			BeanUtils.copyProperties(employee, dtoEmployee);
			response.add(dtoEmployee);
		}
		return response;
	}
}
