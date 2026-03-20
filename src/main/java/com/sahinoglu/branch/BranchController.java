package com.sahinoglu.branch;

import java.util.List;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class BranchController {

	private final BranchService branchService;

	// constructor injection daha iyiymis.
	// lombok.RequiredArgsConstructor da ayni isi yapiyor bu ibretlik dursun burada.
	public BranchController(BranchService branchService) {
		this.branchService = branchService;
	}

	@GetMapping("/branches")
	public List<BranchResponse> list(@RequestParam(name = "active", required = false) Boolean active) {
		if (active != null && active) {

			return branchService.listActive();
		}
		return branchService.listAll();
	}

	// Admin has access to all branches
	@GetMapping("/admin/branches")
	public List<BranchResponse> listAllAdmin() {
		return branchService.listAll();
	}

	@PostMapping("/admin/branches")
	@ResponseStatus(HttpStatus.CREATED)
	public BranchResponse create(@RequestBody BranchRequest request) {
		return branchService.create(request);
	}

	// put yerine patch encouraged imis??
	@PatchMapping("/admin/branches/{id}/deactivate")
	public BranchResponse deactivate(@PathVariable Long id) {
		return branchService.deactivate(id);
	}

	@PatchMapping("/admin/branches/{id}/reactivate")
	public BranchResponse reactivate(@PathVariable Long id) {
		return branchService.reactivate(id);
	}
}