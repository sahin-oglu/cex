package com.sahinoglu.wallet;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sahinoglu.wallet.asset.WalletAssetConversionRequest;
import com.sahinoglu.wallet.asset.WalletAssetConversionResponse;
import com.sahinoglu.wallet.asset.WalletAssetOperationRequest;
import com.sahinoglu.wallet.asset.WalletAssetResponse;
import com.sahinoglu.wallet.asset.WalletAssetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WalletController {

	private final WalletService walletService;
	private final WalletAssetService walletAssetService;

	@GetMapping("/wallets")
	public List<WalletResponse> list() {
		return walletService.list();
	}

	@GetMapping("/wallets/customer/{customerId}")
	public List<WalletResponse> listByCustomer(@PathVariable Long customerId) {

		return walletService.listByCustomer(customerId);
	}

	@PostMapping("/wallets")
	@ResponseStatus(HttpStatus.CREATED)
	public WalletResponse create(@Valid @RequestBody WalletRequest request) {
		return walletService.create(request);
	}

	@PostMapping("/wallets/{walletId}/convert")
	public WalletAssetConversionResponse convert(@PathVariable Long walletId,
			@Valid @RequestBody WalletAssetConversionRequest request) {

		return walletAssetService.convert(walletId, request);
	}

	@PostMapping("/wallets/{walletId}/deposit")
	public WalletAssetResponse depositUsdt(@PathVariable Long walletId,
			@Valid @RequestBody WalletAssetOperationRequest request) {

		return walletAssetService.depositUsdt(walletId, request);
	}

	@PostMapping("/wallets/{walletId}/withdraw")
	public WalletAssetResponse withdrawUsdt(@PathVariable Long walletId,
			@Valid @RequestBody WalletAssetOperationRequest request) {

		return walletAssetService.withdrawUsdt(walletId, request);
	}

	@GetMapping("/wallets/{walletId}/assets")
	public List<WalletAssetResponse> listAssets(@PathVariable Long walletId) {
		return walletAssetService.listAssets(walletId);
	}

	@PatchMapping("/admin/wallets/{id}/deactivate")
	public WalletResponse deactivate(@PathVariable Long id) {

		return walletService.deactivate(id);
	}

	@PatchMapping("/admin/wallets/{id}/reactivate")
	public WalletResponse reactivate(@PathVariable Long id) {

		return walletService.reactivate(id);
	}
}