package com.sahinoglu.security;

import org.springframework.stereotype.Component;

import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.Role;
import com.sahinoglu.exception.ForbiddenException;

@Component
public class ScopeGuard {

	public Long requireCurrentCenterId() {

		Long centerId = SecurityUtils.getCurrentCenterId();

		if (centerId == null) {
			throw new ForbiddenException("Current user is not assigned to a center");
		}

		return centerId;
	}

	public Long requireCurrentBranchId() {

		Long branchId = SecurityUtils.getCurrentBranchId();

		if (branchId == null) {
			throw new ForbiddenException("Current user is not assigned to a branch");
		}

		return branchId;
	}

	public void requireCenterOwnership(Long targetCenterId) {

		Employee current = SecurityUtils.getCurrentEmployee();

		if (current.getRole() == Role.ORG_ADMIN) {
			return;
		}

		if (targetCenterId == null || !targetCenterId.equals(requireCurrentCenterId())) {
			throw new ForbiddenException("Not authorized for this center");
		}
	}

	public void requireBranchOwnership(Long targetBranchId, Long targetCenterId) {

		Employee current = SecurityUtils.getCurrentEmployee();
		Role role = current.getRole();

		if (role == Role.ORG_ADMIN) {
			return;
		}

		if (role == Role.CENTER_ADMIN || role == Role.CENTER_OPERATOR) {
			requireCenterOwnership(targetCenterId);
			return;
		}

		if (role == Role.BRANCH_ADMIN || role == Role.BRANCH_OPERATOR) {
			if (targetBranchId == null || !targetBranchId.equals(requireCurrentBranchId())) {
				throw new ForbiddenException("Not authorized for this branch");
			}
			return;
		}

		throw new ForbiddenException("Unauthorized");
	}
}
