package com.sahinoglu.employee;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class EmployeeController {

	@Autowired
	private EmployeeService service;

	@GetMapping("/employees")
	public List<CreateEmployeeRequest> getEmployeeList() {
		return service.getEmployeeList();
	}

	@PostMapping("/create")
	public CreateEmployeeResponse createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
		return service.createEmployee(request);
	}

}
