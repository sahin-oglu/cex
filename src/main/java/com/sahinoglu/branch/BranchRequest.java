package com.sahinoglu.branch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BranchRequest {
	@NotBlank
	private String name;
	@NotBlank
	private String location;
	@NotNull
	private Long centerId;

}
