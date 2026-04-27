package com.sahinoglu.branch;

import java.util.ArrayList;
import java.util.List;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sahinoglu.center.Center;
import com.sahinoglu.center.CenterRepository;
import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.Role;
import com.sahinoglu.exception.BusinessException;
import com.sahinoglu.exception.ForbiddenException;
import com.sahinoglu.exception.NotFoundException;
import com.sahinoglu.security.SecurityUtils;

@Service
public class BranchService {

	private final BranchRepository repository;

	private final CenterRepository centerRepository;

	public BranchService(BranchRepository repository, CenterRepository centerRepository) {
		this.repository = repository;
		this.centerRepository = centerRepository;
	}

	// kod tekrarini azaltmak icin..
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

	public BranchResponse create(BranchRequest request) {

		Center center = centerRepository.findById(request.getCenterId())
				.orElseThrow(() -> new NotFoundException("Center not found"));

		if (!center.isActive()) {
			throw new BusinessException("Cannot create a branch under inactive center");
		}

		if (repository.existsByNameAndCenterId(request.getName(), request.getCenterId())) {
			throw new BusinessException("Branch name already exists in this center");
		}

		Branch branch = new Branch();
		branch.setName(request.getName());
		branch.setLocation(request.getLocation());
		branch.setCenter(center);
		Branch saved = repository.save(branch);
		return mapToResponse(saved);

	}

	// entity state degistiriliyor, therefore @Transactional
	@Transactional
	public BranchResponse deactivate(Long branchId) {

		Branch branch = repository.findById(branchId).orElseThrow(() -> new NotFoundException("Branch not found"));

		if (!branch.isActive()) {
			throw new BusinessException("Branch already inactive");
		}

		branch.setActive(false);

		return mapToResponse(branch);
	}

	@Transactional
	public BranchResponse reactivate(Long branchId) {
		Branch branch = repository.findById(branchId).orElseThrow(() -> new NotFoundException("Branch not found"));

		if (!branch.getCenter().isActive()) {
			throw new BusinessException("Center inactive");
		}

		if (branch.isActive()) {
			throw new BusinessException("Branch already active");
		}

		branch.setActive(true);

		return mapToResponse(branch);
	}

	public List<BranchResponse> listAll() {
		List<Branch> branchList = repository.findAll();
		List<BranchResponse> response = new ArrayList<>();
		for (Branch branch : branchList) {
//			BranchResponse responseElement = new BranchResponse();
//			responseElement.setName(branch.getName());
//			responseElement.setLocation(branch.getLocation());
//			responseElement.setCenterId(branch.getCenter().getId());
//			responseElement.setId(branch.getId());
//			responseElement.setActive(branch.isActive());
			response.add(mapToResponse(branch));
		}
		return response;
	}

	public List<BranchResponse> listActive() {

		List<Branch> branchList = repository.findByActiveTrue();
		List<BranchResponse> response = new ArrayList<>();
		for (Branch branch : branchList) {
//			BranchResponse responseElement = new BranchResponse();
//			responseElement.setName(branch.getName());
//			responseElement.setLocation(branch.getLocation());
//			responseElement.setCenterId(branch.getCenter().getId());
//			responseElement.setId(branch.getId());
//			responseElement.setActive(branch.isActive());
//			response.add(responseElement);
			response.add(mapToResponse(branch));

		}
		return response;
	}

	public List<BranchResponse> list() {

		Employee current = SecurityUtils.getCurrentEmployee();

		List<Branch> branches;

		if (current.getRole() == Role.ORG_ADMIN) {

			branches = repository.findAll();

		} else if (current.getRole() == Role.CENTER_ADMIN || current.getRole() == Role.CENTER_OPERATOR) {

			Long centerId = SecurityUtils.getCurrentCenterId();

			if (centerId == null) {
				throw new ForbiddenException("Current user is not assigned to a center");
			}

			branches = repository.findListByCenterId(centerId);

		} else if (current.getRole() == Role.BRANCH_ADMIN || current.getRole() == Role.BRANCH_OPERATOR) {

			Long branchId = SecurityUtils.getCurrentBranchId();

			if (branchId == null) {
				throw new ForbiddenException("Current user is not assigned to a branch");
			}

			branches = List
					.of(repository.findById(branchId).orElseThrow(() -> new NotFoundException("Branch not found")));

		} else {
			throw new ForbiddenException("Unauthorized");
		}

		return mapList(branches);
	}

//	private List<Branch> listBranchesByName() {
//		return null;
//	}
}
