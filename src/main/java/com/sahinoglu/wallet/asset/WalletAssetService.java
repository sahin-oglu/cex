package com.sahinoglu.wallet.asset;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sahinoglu.coin.Coin;
import com.sahinoglu.coin.CoinRepository;
import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.Role;
import com.sahinoglu.security.SecurityUtils;
import com.sahinoglu.wallet.Wallet;
import com.sahinoglu.wallet.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletAssetService {

	private final WalletAssetRepository walletAssetRepository;
	private final WalletRepository walletRepository;
	private final CoinRepository coinRepository;

	public List<WalletAssetResponse> listAssets(Long walletId) {

		Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new RuntimeException("Wallet not found"));

		validateWalletScope(wallet);

		return walletAssetRepository.findByWalletId(walletId).stream()
				.map(asset -> new WalletAssetResponse(asset.getWallet().getId(), asset.getCoin().getId(),
						asset.getCoin().getSymbol(), asset.getCoin().getName(), asset.getAmount()))
				.toList();
	}

	private void validateWalletScope(Wallet wallet) {

		Employee current = SecurityUtils.getCurrentEmployee();

		if (current.getRole() == Role.ORG_ADMIN) {
			return;
		}

		if (current.getRole() == Role.CENTER_ADMIN || current.getRole() == Role.CENTER_OPERATOR) {
			Long centerId = SecurityUtils.getCurrentCenterId();

			if (!wallet.getBranch().getCenter().getId().equals(centerId)) {
				throw new RuntimeException("Cannot access wallet from another center");
			}

			return;
		}

		if (current.getRole() == Role.BRANCH_ADMIN || current.getRole() == Role.BRANCH_OPERATOR) {
			Long branchId = SecurityUtils.getCurrentBranchId();

			if (!wallet.getBranch().getId().equals(branchId)) {
				throw new RuntimeException("Cannot access wallet from another branch");
			}

			return;
		}

		throw new RuntimeException("Unauthorized");
	}

	// SADECE DEVELOPMENT ICIN.
	public void seedWalletAssets(Wallet wallet) {
		
		Coin btc = coinRepository.findById("bitcoin").orElseThrow(() -> new RuntimeException("BTC not found"));

		WalletAsset asset = new WalletAsset();
		asset.setWallet(wallet);
		asset.setCoin(btc);
		asset.setAmount(new BigDecimal("0.1"));

		walletAssetRepository.save(asset);
	}
}
