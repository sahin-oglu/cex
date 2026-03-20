package com.sahinoglu.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
//import lombok.AllArgsConstructor;
import lombok.Data;
//import lombok.NoArgsConstructor;

@Data
public class EmployeeRequest {

	@NotBlank
	private String username;

	@NotBlank
	private String password;

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;
	@NotNull

	private Role role;

	private Long branchId;
	// bu business validation'u service katmanina tasiyoruz..
//	@NotNull
	private Long centerId;
}
