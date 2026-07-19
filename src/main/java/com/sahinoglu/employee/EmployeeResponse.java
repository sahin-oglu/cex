package com.sahinoglu.employee;

public record EmployeeResponse(Long id, String username, String firstName, String lastName, Role role,
		Long branchId, Long centerId, boolean active) {
}
