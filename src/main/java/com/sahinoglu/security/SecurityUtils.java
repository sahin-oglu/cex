package com.sahinoglu.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sahinoglu.employee.Employee;

public class SecurityUtils {

	public static Employee getCurrentEmployee() {
		return getCurrentUser().getEmployee();
	}

	public static CustomUserDetails getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new RuntimeException("No authenticated user");
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof CustomUserDetails userDetails) {
			return userDetails;
		}

		throw new RuntimeException("Invalid authentication principal");
	}

	public static Long getCurrentCenterId() {
		Employee e = getCurrentEmployee();

		if (e.getCenter() == null) {
			return null;
		}

		return e.getCenter().getId();
	}

	public static Long getCurrentBranchId() {
		Employee e = getCurrentEmployee();

		if (e.getBranch() == null) {
			return null;
		}

		return e.getBranch().getId();
	}
}