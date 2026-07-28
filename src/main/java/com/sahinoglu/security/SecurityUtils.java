package com.sahinoglu.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sahinoglu.employee.Employee;
import com.sahinoglu.exception.AuthorizationException;

@Component
public class SecurityUtils {

	public Employee getCurrentEmployee() {
		return getCurrentUser().getEmployee();
	}

	public CustomUserDetails getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AuthorizationException("No authenticated user");
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof CustomUserDetails userDetails) {
			return userDetails;
		}

		throw new AuthorizationException("Invalid authentication principal");
	}

	public Long getCurrentCenterId() {
		Employee e = getCurrentEmployee();

		if (e.getCenter() == null) {
			return null;
		}

		return e.getCenter().getId();
	}

	public Long getCurrentBranchId() {
		Employee e = getCurrentEmployee();

		if (e.getBranch() == null) {
			return null;
		}

		return e.getBranch().getId();
	}
}
