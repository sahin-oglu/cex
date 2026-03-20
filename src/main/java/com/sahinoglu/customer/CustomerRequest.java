package com.sahinoglu.customer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRequest {

	@NotBlank
	private String name;

	@NotBlank
	private String phone;
}