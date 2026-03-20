package com.sahinoglu.center;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CenterController {

	private final CenterService service;

	// Active list (non-admin kullanım)
	@GetMapping("/centers")
	public List<CenterResponse> list(@RequestParam(name = "active", required = false) Boolean active) {
		if (active != null && active) {
			return service.listActive();
		}
		return service.listAll();
	}

	// Management: create (ORG_ADMIN)
	@PostMapping("/admin/centers")
	public CenterResponse create(@RequestBody CenterRequest request) {
		return service.create(request);
	}

	@PatchMapping("/admin/centers/{id}/deactivate")
	public CenterResponse deactivate(@PathVariable Long id) {
		return service.deactivate(id);
	}

	@PatchMapping("/admin/centers/{id}/reactivate")
	public CenterResponse reactivate(@PathVariable Long id) {
		return service.reactivate(id);
	}
}