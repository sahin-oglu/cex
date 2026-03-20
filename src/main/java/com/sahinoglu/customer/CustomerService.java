package com.sahinoglu.customer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository repository;

	private CustomerResponse mapToResponse(Customer customer) {
		return new CustomerResponse(customer.getId(), customer.getName(), customer.getPhone());
	}

	public CustomerResponse create(CustomerRequest request) {

		Customer customer = new Customer();
		customer.setName(request.getName());
		customer.setPhone(request.getPhone());

		Customer saved = repository.save(customer);

		return mapToResponse(saved);
	}

	public List<CustomerResponse> listAll() {

		List<Customer> customers = repository.findAll();
		List<CustomerResponse> response = new ArrayList<>();

		for (Customer customer : customers) {
			response.add(mapToResponse(customer));
		}

		return response;
	}

}