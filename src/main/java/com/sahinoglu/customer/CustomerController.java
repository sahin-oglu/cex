package com.sahinoglu.customer;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerService customerService;
	
	@GetMapping("/admin/customers")
	public List<CustomerResponse> list() {
		return customerService.listAll();
	}

	@PostMapping("/admin/customers")
	@ResponseStatus(HttpStatus.CREATED)
	public CustomerResponse create(@Valid @RequestBody CustomerRequest request) {

		return customerService.create(request);
	}

}