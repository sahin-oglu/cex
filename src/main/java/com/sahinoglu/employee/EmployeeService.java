package com.sahinoglu.employee;

import java.util.ArrayList;
import java.util.List;

//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sahinoglu.branch.Branch;
import com.sahinoglu.branch.BranchRepository;
import com.sahinoglu.center.Center;
import com.sahinoglu.center.CenterRepository;
import com.sahinoglu.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

	private final EmployeeRepository repository;
	private final BranchRepository branchRepository;
	private final CenterRepository centerRepository;

	// org_admin'in center'i null oldugu icin.
	private EmployeeResponse mapToResponse(Employee employee) {
		EmployeeResponse response = new EmployeeResponse();
		response.setId(employee.getId());
		response.setUsername(employee.getUsername());
		response.setFirstName(employee.getFirstName());
		response.setLastName(employee.getLastName());
		response.setRole(employee.getRole());
		if (employee.getCenter() != null) {
			response.setCenterId(employee.getCenter().getId());
		}

		if (employee.getBranch() != null) {
			response.setBranchId(employee.getBranch().getId());
		}

		return response;
	}

	private List<EmployeeResponse> mapList(List<Employee> employees) {

		List<EmployeeResponse> responses = new ArrayList<>();

		for (Employee e : employees) {
			responses.add(mapToResponse(e));
		}

		return responses;
	}

	public EmployeeResponse create(EmployeeRequest request) {
		// BUTUN BUNLARI validate()'e tasidim!!

//		if (repository.findByUsername(request.getUsername()).isPresent()) {
//			throw new RuntimeException("Username already exists");
//		}
//
//		Center center = centerRepository.findById(request.getCenterId())
//				.orElseThrow(() -> new RuntimeException("Center not found"));
//
//		if (!center.isActive()) {
//			throw new RuntimeException("Center is inactive");
//		}
//
//		// biraz tuhaf duruyor belki baska sekilde de bu data integrity halledilebilir?
//		Role role = request.getRole();
//		boolean isBranchRole = (role == Role.BRANCH_OPERATOR || role == Role.BRANCH_ADMIN);
//		boolean isCenterRole = (role == Role.CENTER_OPERATOR || role == Role.CENTER_ADMIN);
//		if (isBranchRole && request.getBranchId() == null) {
//			throw new RuntimeException("Branch is required for branch roles");
//		}
//		if (isCenterRole && request.getBranchId() != null) {
//			throw new RuntimeException("Branch must be null for center roles");
//		}
		validate(request);

		Employee employee = new Employee();
		employee.setUsername(request.getUsername());
		employee.setPassword(request.getPassword());
		employee.setFirstName(request.getFirstName());
		employee.setLastName(request.getLastName());
		employee.setRole(request.getRole());

		if (request.getCenterId() != null) {
			Center center = centerRepository.findById(request.getCenterId())
					.orElseThrow(() -> new RuntimeException("Center not found"));
			employee.setCenter(center);
		}

		if (request.getBranchId() != null) {
			Branch branch = branchRepository.findById(request.getBranchId())
					.orElseThrow(() -> new RuntimeException("Branch not found"));
			employee.setBranch(branch);
		}

		Employee saved = repository.save(employee);
		return mapToResponse(saved);
	}
	// scope yok bunda, sifirdan oburunu yazicam bismillah..
//	public List<EmployeeResponse> getList() {
//		List<Employee> employeeList = repository.findAll();
//		List<EmployeeResponse> response = new ArrayList<>();
//
//		for (Employee employee : employeeList) {
//			// beanutils discouraged imis.
////			EmployeeResponse responseElement = new EmployeeResponse();
////			BeanUtils.copyProperties(employee, responseElement);
//			response.add(mapToResponse(employee));
//		}
//		return response;
//	}

//	public List<EmployeeResponse> list() {
//
//		Employee current = SecurityUtils.getCurrentUser().getEmployee();
//
//		List<Employee> employees;
//
//		if (current.getRole() == Role.ORG_ADMIN) {
//			employees = repository.findAll();
//
//		} else if (current.getRole() == Role.CENTER_ADMIN || current.getRole() == Role.CENTER_OPERATOR) {
//			employees = repository.findByCenterId(current.getCenter().getId());
//
//		} else if (current.getRole() == Role.BRANCH_ADMIN || current.getRole() == Role.BRANCH_OPERATOR) {
//			employees = repository.findByBranchId(current.getBranch().getId());
//
//		} else {
//			throw new RuntimeException("Unauthorized");
//		}
//
//		List<EmployeeResponse> responseList = new ArrayList<>();
//
//		for (Employee employee : employees) {
//			responseList.add(mapToResponse(employee));
//		}
//
//		return responseList;
//	}

	public List<EmployeeResponse> list() {

		Employee current = SecurityUtils.getCurrentEmployee();

		List<Employee> employees;

		if (current.getRole() == Role.ORG_ADMIN) {

			employees = repository.findAll();

		} else if (current.getRole() == Role.CENTER_ADMIN || current.getRole() == Role.CENTER_OPERATOR) {

			Long centerId = SecurityUtils.getCurrentCenterId();

			if (centerId == null) {
				throw new RuntimeException("Center not found in session");
			}

			employees = repository.findByCenterId(centerId);

		} else if (current.getRole() == Role.BRANCH_ADMIN || current.getRole() == Role.BRANCH_OPERATOR) {

			Long branchId = SecurityUtils.getCurrentBranchId();

			if (branchId == null) {
				throw new RuntimeException("Branch not found in session");
			}

			employees = repository.findByBranchId(branchId);

		} else {
			throw new RuntimeException("Unauthorized");
		}

		return mapList(employees);
	}

	private void validate(EmployeeRequest request) {

		validateRoleRules(request);

		if (request.getBranchId() != null && request.getCenterId() != null) {
			validateBranchCenterRelation(request.getBranchId(), request.getCenterId());
		}

		validateUsernameUniqueness(request.getUsername());
	}

	private void validateRoleRules(EmployeeRequest request) {

		Role role = request.getRole();

		Long centerId = request.getCenterId();
		Long branchId = request.getBranchId();

		switch (role) {

		case ORG_ADMIN -> {

			if (centerId != null || branchId != null) {
				throw new RuntimeException("ORG_ADMIN cannot belong to center or branch");
			}
		}

		case CENTER_ADMIN, CENTER_OPERATOR -> {

			if (centerId == null) {
				throw new RuntimeException(role + " must belong to a center");
			}

			if (branchId != null) {
				throw new RuntimeException(role + " cannot belong to a branch");
			}
		}

		case BRANCH_ADMIN, BRANCH_OPERATOR -> {

			if (centerId == null) {
				throw new RuntimeException(role + " must belong to a center");
			}

			if (branchId == null) {
				throw new RuntimeException(role + " must belong to a branch");
			}
		}
		}
	}

	private void validateBranchCenterRelation(Long branchId, Long centerId) {

		Branch branch = branchRepository.findById(branchId).orElseThrow(() -> new RuntimeException("Branch not found"));

		if (!branch.getCenter().getId().equals(centerId)) {
			throw new RuntimeException("Branch does not belong to given center");
		}
	}

	private void validateUsernameUniqueness(String username) {

		if (repository.existsByUsername(username)) {
			throw new RuntimeException("Username already exists");
		}
	}

}
