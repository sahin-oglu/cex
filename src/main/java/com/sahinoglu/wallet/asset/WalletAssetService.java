package com.sahinoglu.wallet.asset;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sahinoglu.coin.Coin;
import com.sahinoglu.coin.CoinRepository;
import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.Role;
import com.sahinoglu.exception.BusinessException;
import com.sahinoglu.exception.ForbiddenException;
import com.sahinoglu.exception.NotFoundException;
import com.sahinoglu.security.SecurityUtils;
import com.sahinoglu.wallet.Wallet;
import com.sahinoglu.wallet.WalletRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletAssetService {

	private final WalletAssetRepository walletAssetRepository;
	private final WalletRepository walletRepository;
	private final CoinRepository coinRepository;
	private static final String USDT_ID = "tether";

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
//	public void seedWalletAssets(Wallet wallet) {
//		
//		Coin btc = coinRepository.findById("bitcoin").orElseThrow(() -> new RuntimeException("BTC not found"));
//
//		WalletAsset asset = new WalletAsset();
//		asset.setWallet(wallet);
//		asset.setCoin(btc);
//		asset.setAmount(new BigDecimal("0.1"));
//
//		walletAssetRepository.save(asset);
//	}

	@Transactional
	public WalletAssetConversionResponse convert(Long walletId, WalletAssetConversionRequest request) {

		Wallet wallet = walletRepository.findById(walletId)
				.orElseThrow(() -> new NotFoundException("Wallet not found"));

		validateBranchOperatorWalletAccess(wallet);
		validateWalletUsable(wallet);

		if (request.getFromCoinId().equals(request.getToCoinId())) {
			throw new BusinessException("Cannot convert asset to itself");
		}

		Coin fromCoin = coinRepository.findById(request.getFromCoinId())
				.orElseThrow(() -> new NotFoundException("Source coin not found"));

		Coin toCoin = coinRepository.findById(request.getToCoinId())
				.orElseThrow(() -> new NotFoundException("Target coin not found"));

		validateCoinPriceAvailable(fromCoin);
		validateCoinPriceAvailable(toCoin);

		WalletAsset fromAsset = walletAssetRepository.findByWalletAndCoin(wallet, fromCoin)
				.orElseThrow(() -> new BusinessException("Source asset not found in wallet"));

		if (fromAsset.getAmount().compareTo(request.getAmount()) < 0) {
			throw new BusinessException("Insufficient balance for conversion");
		}

		BigDecimal feeRate = new BigDecimal("0.01");
		BigDecimal feeAmount = request.getAmount().multiply(feeRate);
		BigDecimal netFromAmount = request.getAmount().subtract(feeAmount);

		BigDecimal usdValue = netFromAmount.multiply(fromCoin.getPrice());

		BigDecimal receivedAmount = usdValue.divide(toCoin.getPrice(), 8, RoundingMode.DOWN);

		fromAsset.setAmount(fromAsset.getAmount().subtract(request.getAmount()));

		WalletAsset toAsset = walletAssetRepository.findByWalletAndCoin(wallet, toCoin).orElseGet(() -> {
			WalletAsset newAsset = new WalletAsset();
			newAsset.setWallet(wallet);
			newAsset.setCoin(toCoin);
			newAsset.setAmount(BigDecimal.ZERO);
			return newAsset;
		});

		toAsset.setAmount(toAsset.getAmount().add(receivedAmount));

		walletAssetRepository.save(fromAsset);
		WalletAsset savedToAsset = walletAssetRepository.save(toAsset);

		return new WalletAssetConversionResponse(wallet.getId(), fromCoin.getId(), fromCoin.getSymbol(), toCoin.getId(),
				toCoin.getSymbol(), request.getAmount(), feeAmount, netFromAmount, receivedAmount,
				savedToAsset.getAmount());
	}

	@Transactional
	public WalletAssetResponse depositUsdt(Long walletId, WalletAssetOperationRequest request) {

		Wallet wallet = walletRepository.findById(walletId)
				.orElseThrow(() -> new NotFoundException("Wallet not found"));

		validateBranchOperatorWalletAccess(wallet);
		validateWalletUsable(wallet);

		Coin usdt = coinRepository.findById(USDT_ID).orElseThrow(() -> new NotFoundException("USDT not found"));

		WalletAsset asset = walletAssetRepository.findByWalletAndCoin(wallet, usdt).orElseGet(() -> {
			WalletAsset newAsset = new WalletAsset();
			newAsset.setWallet(wallet);
			newAsset.setCoin(usdt);
			newAsset.setAmount(BigDecimal.ZERO);
			return newAsset;
		});

		asset.setAmount(asset.getAmount().add(request.getAmount()));

		WalletAsset saved = walletAssetRepository.save(asset);

		return mapToResponse(saved);
	}

	@Transactional
	public WalletAssetResponse withdrawUsdt(Long walletId, WalletAssetOperationRequest request) {

		Wallet wallet = walletRepository.findById(walletId)
				.orElseThrow(() -> new NotFoundException("Wallet not found"));

		validateBranchOperatorWalletAccess(wallet);
		validateWalletUsable(wallet);

		Coin usdt = coinRepository.findById(USDT_ID).orElseThrow(() -> new NotFoundException("USDT not found"));

		WalletAsset asset = walletAssetRepository.findByWalletAndCoin(wallet, usdt)
				.orElseThrow(() -> new BusinessException("No USDT asset found for this wallet"));

		if (asset.getAmount().compareTo(request.getAmount()) < 0) {
			throw new BusinessException("Insufficient USDT balance");
		}

		asset.setAmount(asset.getAmount().subtract(request.getAmount()));

		WalletAsset saved = walletAssetRepository.save(asset);

		return mapToResponse(saved);
	}

	private WalletAssetResponse mapToResponse(WalletAsset asset) {
		return new WalletAssetResponse(asset.getWallet().getId(), asset.getCoin().getId(), asset.getCoin().getSymbol(),
				asset.getCoin().getName(), asset.getAmount());
	}

	private void validateBranchOperatorWalletAccess(Wallet wallet) {

		Employee current = SecurityUtils.getCurrentEmployee();

		if (current.getRole() != Role.BRANCH_OPERATOR) {
			throw new ForbiddenException("Only branch operator can perform wallet asset operations");
		}

		Long branchId = SecurityUtils.getCurrentBranchId();

		if (branchId == null) {
			throw new ForbiddenException("Current user is not assigned to a branch");
		}

		if (!wallet.getBranch().getId().equals(branchId)) {
			throw new ForbiddenException("Cannot operate on wallet from another branch");
		}
	}

	private void validateWalletUsable(Wallet wallet) {

		if (!wallet.isActive()) {
			throw new BusinessException("Wallet is inactive");
		}

		if (!wallet.getBranch().isActive()) {
			throw new BusinessException("Branch is inactive");
		}

		if (!wallet.getBranch().getCenter().isActive()) {
			throw new BusinessException("Center is inactive");
		}
	}

	private void validateCoinPriceAvailable(Coin coin) {

		if (coin.getPrice() == null || coin.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
			throw new BusinessException("Coin price is not available");
		}
	}
}
