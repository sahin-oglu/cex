package com.sahinoglu.center;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CenterRequest {

	@NotBlank
	private String name;

	@NotBlank
	private String location;
}