package com.sahinoglu.branch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sahinoglu.center.Center;
import com.sahinoglu.center.CenterRepository;
import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.Role;
import com.sahinoglu.exception.BusinessException;
import com.sahinoglu.exception.ForbiddenException;
import com.sahinoglu.exception.NotFoundException;
import com.sahinoglu.security.ScopeGuard;
import com.sahinoglu.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchService {

	private final BranchRepository branchRepository;

	private final CenterRepository centerRepository;

	private final ScopeGuard scopeGuard;

	private final SecurityUtils securityUtils;

	public BranchResponse create(BranchRequest request) {

		Center center = centerRepository.findById(request.getCenterId())
				.orElseThrow(() -> new NotFoundException("Center not found"));

		if (!center.isActive()) {
			throw new BusinessException("Cannot create a branch under inactive center");
		}

		if (branchRepository.existsByNameAndCenterId(request.getName(), request.getCenterId())) {
			throw new BusinessException("Branch name already exists in this center");
		}

		Branch branch = new Branch();
		branch.setName(request.getName());
		branch.setLocation(request.getLocation());
		branch.setCenter(center);
		Branch saved = branchRepository.save(branch);
		return mapToResponse(saved);

	}

	@Transactional
	public BranchResponse deactivate(Long branchId) {

		Branch branch = branchRepository.findById(branchId).orElseThrow(() -> new NotFoundException("Branch not found"));

		scopeGuard.requireCenterOwnership(branch.getCenter().getId());

		if (!branch.isActive()) {
			throw new BusinessException("Branch already inactive");
		}

		branch.setActive(false);

		return mapToResponse(branch);
	}

	@Transactional
	public BranchResponse reactivate(Long branchId) {
		Branch branch = branchRepository.findById(branchId).orElseThrow(() -> new NotFoundException("Branch not found"));

		scopeGuard.requireCenterOwnership(branch.getCenter().getId());

		if (!branch.getCenter().isActive()) {
			throw new BusinessException("Center inactive");
		}

		if (branch.isActive()) {
			throw new BusinessException("Branch already active");
		}

		branch.setActive(true);

		return mapToResponse(branch);
	}

	public List<BranchResponse> listActive() {

		List<Branch> branchList = branchRepository.findByActiveTrue();
		List<BranchResponse> response = new ArrayList<>();
		for (Branch branch : branchList) {
			response.add(mapToResponse(branch));

		}
		return response;
	}

	public List<BranchResponse> list() {

		Employee current = securityUtils.getCurrentEmployee();

		List<Branch> branches;

		if (current.getRole() == Role.ORG_ADMIN) {

			branches = branchRepository.findAll();

		} else if (current.getRole() == Role.CENTER_ADMIN || current.getRole() == Role.CENTER_OPERATOR) {

			branches = branchRepository.findListByCenterId(scopeGuard.requireCurrentCenterId());

		} else if (current.getRole() == Role.BRANCH_ADMIN || current.getRole() == Role.BRANCH_OPERATOR) {

			Long branchId = scopeGuard.requireCurrentBranchId();

			branches = List
					.of(branchRepository.findById(branchId).orElseThrow(() -> new NotFoundException("Branch not found")));

		} else {
			throw new ForbiddenException("Unauthorized");
		}

		return mapList(branches);
	}

	private BranchResponse mapToResponse(Branch branch) {
		return new BranchResponse(branch.getId(), branch.getName(), branch.getLocation(), branch.getCenter().getId(),
				branch.isActive());
	}

	private List<BranchResponse> mapList(List<Branch> branches) {

		List<BranchResponse> responses = new ArrayList<>();

		for (Branch b : branches) {
			responses.add(mapToResponse(b));
		}

		return responses;
	}
}
