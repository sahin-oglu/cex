package com.sahinoglu.center;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CenterResponse {
	private Long id;
	private String name;
	private String location;
	private boolean active;
}
