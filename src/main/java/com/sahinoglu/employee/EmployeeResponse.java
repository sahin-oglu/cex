package com.sahinoglu.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {

	private Long id;

	private String username;

	private String firstName;

	private String lastName;

	private Role role;

	private Long branchId;
	private Long centerId;
}
