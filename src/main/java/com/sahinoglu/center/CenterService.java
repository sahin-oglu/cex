package com.sahinoglu.center;

import java.util.ArrayList;
import java.util.List;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.Role;
import com.sahinoglu.exception.BusinessException;
import com.sahinoglu.exception.ForbiddenException;
import com.sahinoglu.exception.NotFoundException;
import com.sahinoglu.security.SecurityUtils;

//import com.sahinoglu.center.CenterRequest;
//import com.sahinoglu.center.CenterResponse;
//import com.sahinoglu.branch.Branch;
//import com.sahinoglu.branch.BranchRepository;
//import com.sahinoglu.branch.BranchResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CenterService {

	private final CenterRepository repository;

//	private final BranchRepository branchRepository;

	private CenterResponse mapToResponse(Center center) {
		return new CenterResponse(center.getId(), center.getName(), center.getLocation(), center.isActive());
	}

	private List<CenterResponse> mapList(List<Center> centers) {

		List<CenterResponse> responses = new ArrayList<>();

		for (Center c : centers) {
			responses.add(mapToResponse(c));
		}

		return responses;
	}

	public CenterResponse create(CenterRequest request) {

		if (repository.existsByName(request.getName())) {
			throw new BusinessException("Center already exists");
		}

		Center center = new Center();
		center.setName(request.getName());
		center.setLocation(request.getLocation());
		Center saved = repository.save(center);
		return mapToResponse(saved);
	}

	public List<CenterResponse> listAll() {
		List<Center> centerList = repository.findAll();
		List<CenterResponse> response = new ArrayList<>();
		for (Center center : centerList) {
			response.add(mapToResponse(center));
		}

		return response;
	}

	public List<CenterResponse> listActive() {
		List<Center> centerList = repository.findByActiveTrue();
		List<CenterResponse> response = new ArrayList<>();
		for (Center center : centerList) {

			response.add(mapToResponse(center));

		}
		return response;
	}

	// şimdilik unused, controller'a sonra implement edeceğim.
	public List<CenterResponse> list() {

		Employee current = SecurityUtils.getCurrentEmployee();

		List<Center> centers;

		if (current.getRole() == Role.ORG_ADMIN) {

			centers = repository.findAll();

		} else if (current.getRole() == Role.CENTER_ADMIN || current.getRole() == Role.CENTER_OPERATOR) {

			Long centerId = SecurityUtils.getCurrentCenterId();

			if (centerId == null) {
				throw new ForbiddenException("Current user is not assigned to a center");
			}

			centers = List
					.of(repository.findById(centerId).orElseThrow(() -> new NotFoundException("Center not found")));

		} else if (current.getRole() == Role.BRANCH_ADMIN || current.getRole() == Role.BRANCH_OPERATOR) {

			Long centerId = SecurityUtils.getCurrentCenterId();

			if (centerId == null) {
				throw new ForbiddenException("Current user is not assigned to a center");
			}

			centers = List
					.of(repository.findById(centerId).orElseThrow(() -> new NotFoundException("Center not found")));

		} else {
			throw new ForbiddenException("Unauthorized");
		}

		return mapList(centers);
	}

	@Transactional
	public CenterResponse deactivate(Long centerId) {

		Center center = repository.findById(centerId).orElseThrow(() -> new NotFoundException("Center not found"));

		if (!center.isActive()) {
			throw new BusinessException("Center already inactive");
		}

		center.setActive(false);

		// branch'leri de inactive yapacagim sonra..
//		branchRepository.deactivateByCenterId(center.getId());

		return mapToResponse(center);
	}

	@Transactional
	public CenterResponse reactivate(Long id) {

		Center center = repository.findById(id).orElseThrow(() -> new NotFoundException("Center not found"));

		if (center.isActive()) {
			throw new BusinessException("Center already active");
		}

		center.setActive(true);

		return mapToResponse(center);
	}

}