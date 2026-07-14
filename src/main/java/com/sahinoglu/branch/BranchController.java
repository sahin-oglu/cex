package com.sahinoglu.branch;

import java.util.List;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BranchController {

	private final BranchService branchService;

	@GetMapping("/branches")
	public List<BranchResponse> listActive() {

		return branchService.listActive();
	}

	@GetMapping("/admin/branches")
	public List<BranchResponse> listAllAdmin() {
		return branchService.listAll();
	}

	@PostMapping("/admin/branches")
	@ResponseStatus(HttpStatus.CREATED)
	public BranchResponse create(@Valid @RequestBody BranchRequest request) {
		return branchService.create(request);
	}

	@PatchMapping("/admin/branches/{id}/deactivate")
	public BranchResponse deactivate(@PathVariable Long id) {
		return branchService.deactivate(id);
	}

	@PatchMapping("/admin/branches/{id}/reactivate")
	public BranchResponse reactivate(@PathVariable Long id) {
		return branchService.reactivate(id);
	}
}