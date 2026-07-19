package com.sahinoglu.employee;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sahinoglu.branch.Branch;
import com.sahinoglu.branch.BranchRepository;
import com.sahinoglu.center.Center;
import com.sahinoglu.center.CenterRepository;
import com.sahinoglu.exception.BusinessException;
import com.sahinoglu.exception.ForbiddenException;
import com.sahinoglu.exception.NotFoundException;
import com.sahinoglu.security.ScopeGuard;
import com.sahinoglu.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

	private final EmployeeRepository repository;
	private final BranchRepository branchRepository;
	private final CenterRepository centerRepository;

	private final PasswordEncoder passwordEncoder;
	private final ScopeGuard scopeGuard;

	public EmployeeResponse create(EmployeeRequest request) {

		validate(request);

		Employee employee = new Employee();
		employee.setUsername(request.getUsername());
		employee.setPassword(passwordEncoder.encode(request.getPassword()));
		employee.setFirstName(request.getFirstName());
		employee.setLastName(request.getLastName());
		employee.setRole(request.getRole());

		switch (request.getRole()) {

		case ORG_ADMIN -> {
			// center and branch fields are null for ORG_ADMIN.
		}

		case CENTER_ADMIN, CENTER_OPERATOR -> {
			Center center = centerRepository.findById(request.getCenterId())
					.orElseThrow(() -> new NotFoundException("Center not found"));

			employee.setCenter(center);
		}

		case BRANCH_ADMIN, BRANCH_OPERATOR -> {
			Branch branch = branchRepository.findById(request.getBranchId())
					.orElseThrow(() -> new NotFoundException("Branch not found"));

			employee.setBranch(branch);
			employee.setCenter(branch.getCenter());
		}
		}

		Employee saved = repository.save(employee);
		return mapToResponse(saved);
	}

	public List<EmployeeResponse> list() {

		Employee current = SecurityUtils.getCurrentEmployee();

		List<Employee> employees;

		if (current.getRole() == Role.ORG_ADMIN) {

			employees = repository.findAll();

		} else if (current.getRole() == Role.CENTER_ADMIN || current.getRole() == Role.CENTER_OPERATOR) {

			employees = repository.findByCenterId(scopeGuard.requireCurrentCenterId());

		} else if (current.getRole() == Role.BRANCH_ADMIN || current.getRole() == Role.BRANCH_OPERATOR) {

			employees = repository.findByBranchId(scopeGuard.requireCurrentBranchId());

		} else {
			throw new ForbiddenException("Unauthorized");
		}

		return mapList(employees);
	}

	@Transactional
	public EmployeeResponse deactivate(Long employeeId) {

		Employee employee = repository.findById(employeeId)
				.orElseThrow(() -> new NotFoundException("Employee not found"));

		scopeGuard.requireCenterOwnership(employee.getCenter() != null ? employee.getCenter().getId() : null);

		if (!employee.isActive()) {
			throw new BusinessException("Employee already inactive");
		}

		employee.setActive(false);

		return mapToResponse(employee);
	}

	@Transactional
	public EmployeeResponse reactivate(Long employeeId) {

		Employee employee = repository.findById(employeeId)
				.orElseThrow(() -> new NotFoundException("Employee not found"));

		scopeGuard.requireCenterOwnership(employee.getCenter() != null ? employee.getCenter().getId() : null);

		if (employee.isActive()) {
			throw new BusinessException("Employee already active");
		}

		if (employee.getBranch() != null && !employee.getBranch().isActive()) {
			throw new BusinessException("Cannot reactivate employee while branch is inactive");
		}

		if (employee.getCenter() != null && !employee.getCenter().isActive()) {
			throw new BusinessException("Cannot reactivate employee while center is inactive");
		}

		employee.setActive(true);

		return mapToResponse(employee);
	}

	private void validate(EmployeeRequest request) {

		validateRoleRules(request);

		validateUsernameUniqueness(request.getUsername());
	}

	private void validateRoleRules(EmployeeRequest request) {

		Role role = request.getRole();

		Long centerId = request.getCenterId();
		Long branchId = request.getBranchId();

		switch (role) {

		case ORG_ADMIN -> {

			if (centerId != null || branchId != null) {
				throw new BusinessException("ORG_ADMIN cannot belong to center or branch");
			}
		}

		case CENTER_ADMIN, CENTER_OPERATOR -> {

			if (centerId == null) {
				throw new BusinessException(role + " must belong to a center");
			}

			if (branchId != null) {
				throw new BusinessException(role + " cannot belong to a branch");
			}
		}

		case BRANCH_ADMIN, BRANCH_OPERATOR -> {
			if (branchId == null) {
				throw new BusinessException(role + " must belong to a branch");
			}

			if (centerId != null) {
				throw new BusinessException(role + " must not receive a separate center");
			}
		}
		}
	}

	private void validateUsernameUniqueness(String username) {

		if (repository.existsByUsername(username)) {
			throw new BusinessException("Username already exists");
		}
	}

	private EmployeeResponse mapToResponse(Employee employee) {
		Long branchId = employee.getBranch() != null ? employee.getBranch().getId() : null;
		Long centerId = employee.getCenter() != null ? employee.getCenter().getId() : null;

		return new EmployeeResponse(employee.getId(), employee.getUsername(), employee.getFirstName(),
				employee.getLastName(), employee.getRole(), branchId, centerId, employee.isActive());
	}

	private List<EmployeeResponse> mapList(List<Employee> employees) {

		List<EmployeeResponse> responses = new ArrayList<>();

		for (Employee e : employees) {
			responses.add(mapToResponse(e));
		}

		return responses;
	}
}
