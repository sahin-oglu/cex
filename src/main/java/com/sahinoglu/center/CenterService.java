package com.sahinoglu.center;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sahinoglu.branch.BranchRepository;
import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.Role;
import com.sahinoglu.exception.BusinessException;
import com.sahinoglu.exception.ForbiddenException;
import com.sahinoglu.exception.NotFoundException;
import com.sahinoglu.security.ScopeGuard;
import com.sahinoglu.security.SecurityUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CenterService {

	private final CenterRepository centerRepository;
	private final BranchRepository branchRepository;
	private final ScopeGuard scopeGuard;

	private final SecurityUtils securityUtils;

	public CenterResponse create(CenterRequest request) {

		if (centerRepository.existsByName(request.getName())) {
			throw new BusinessException("Center already exists");
		}

		Center center = new Center();
		center.setName(request.getName());
		center.setLocation(request.getLocation());
		Center saved = centerRepository.save(center);
		return mapToResponse(saved);
	}

	public List<CenterResponse> listActive() {
		List<Center> centerList = centerRepository.findByActiveTrue();
		List<CenterResponse> response = new ArrayList<>();
		for (Center center : centerList) {

			response.add(mapToResponse(center));
			
		}
		return response;
	}

	public List<CenterResponse> list() {

		Employee current = securityUtils.getCurrentEmployee();

		List<Center> centers;

		if (current.getRole() == Role.ORG_ADMIN) {

			centers = centerRepository.findAll();

		} else if (current.getRole() == Role.CENTER_ADMIN || current.getRole() == Role.CENTER_OPERATOR
				|| current.getRole() == Role.BRANCH_ADMIN || current.getRole() == Role.BRANCH_OPERATOR) {

			Long centerId = scopeGuard.requireCurrentCenterId();

			centers = List.of(
					centerRepository.findById(centerId).orElseThrow(() -> new NotFoundException("Center not found")));

		} else {
			throw new ForbiddenException("Unauthorized");
		}

		return mapList(centers);
	}

	@Transactional
	public CenterResponse deactivate(Long centerId) {

		Center center = centerRepository.findById(centerId)
				.orElseThrow(() -> new NotFoundException("Center not found"));

		if (!center.isActive()) {
			throw new BusinessException("Center already inactive");
		}

		center.setActive(false);

		// also deactivate the branches that are assigned to this center.
		branchRepository.deactivateBranchesByCenterId(centerId);

		return mapToResponse(center);
	}

	@Transactional
	public CenterResponse reactivate(Long id) {

		Center center = centerRepository.findById(id).orElseThrow(() -> new NotFoundException("Center not found"));

		if (center.isActive()) {
			throw new BusinessException("Center already active");
		}

		center.setActive(true);

		return mapToResponse(center);
	}
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

}