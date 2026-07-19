package com.sahinoglu.employee;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

	private final EmployeeService employeeService;

	@GetMapping("/admin/employees")
	public List<EmployeeResponse> list() {
		return employeeService.list();
	}

	@PostMapping("/admin/employees")
	@ResponseStatus(HttpStatus.CREATED)
	public EmployeeResponse create(@Valid @RequestBody EmployeeRequest request) {
		return employeeService.create(request);
	}

	@PatchMapping("/admin/employees/{id}/deactivate")
	public EmployeeResponse deactivate(@PathVariable Long id) {
		return employeeService.deactivate(id);
	}

	@PatchMapping("/admin/employees/{id}/reactivate")
	public EmployeeResponse reactivate(@PathVariable Long id) {
		return employeeService.reactivate(id);
	}

}
