package com.sahinoglu.center;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CenterController {

	private final CenterService centerService;

	@GetMapping("/centers")
	public List<CenterResponse> listActive() {

		return centerService.listActive();
	}

	@GetMapping("/admin/centers")
	public List<CenterResponse> listAllAdmin() {
		return centerService.listAll();
	}

	@PostMapping("/admin/centers")
	@ResponseStatus(HttpStatus.CREATED)
	public CenterResponse create(@Valid @RequestBody CenterRequest request) {
		return centerService.create(request);
	}

	@PatchMapping("/admin/centers/{id}/deactivate")
	public CenterResponse deactivate(@PathVariable Long id) {
		return centerService.deactivate(id);
	}

	@PatchMapping("/admin/centers/{id}/reactivate")
	public CenterResponse reactivate(@PathVariable Long id) {
		return centerService.reactivate(id);
	}
}