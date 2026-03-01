package com.sahinoglu.employee;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class CreateEmployeeResponse {

	private Integer id;
	
	private String username;
	
	private String firstName;
	
	private String lastName;
	
	private Role role;
}
