package com.sahinoglu.branch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchResponse {

	private Long id;
	private String name;
	private String location;
	private long centerId;
	private boolean active;

}
