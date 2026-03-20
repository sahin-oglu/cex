package com.sahinoglu.employee;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

	private final EmployeeService service;

	@GetMapping("/admin/employees")
	public List<EmployeeResponse> getList() {
		return service.list();
	}

	@PostMapping("/admin/employees")
	public EmployeeResponse create(@Valid @RequestBody EmployeeRequest request) {
		return service.create(request);
	}

}
