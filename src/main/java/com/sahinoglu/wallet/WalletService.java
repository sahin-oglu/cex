package com.sahinoglu.wallet;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sahinoglu.branch.Branch;
import com.sahinoglu.branch.BranchRepository;
import com.sahinoglu.coin.CoinRepository;
import com.sahinoglu.customer.Customer;
import com.sahinoglu.customer.CustomerRepository;
import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.Role;
import com.sahinoglu.exception.BusinessException;
import com.sahinoglu.exception.ForbiddenException;
import com.sahinoglu.exception.NotFoundException;
import com.sahinoglu.security.SecurityUtils;
import com.sahinoglu.wallet.asset.WalletAssetRepository;
import com.sahinoglu.wallet.asset.WalletAssetService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class WalletService {

	private final WalletRepository repository;
	private final CustomerRepository customerRepository;
	private final BranchRepository branchRepository;
	private final WalletAssetService walletAssetService;

	private WalletResponse mapToResponse(Wallet wallet) {
		return new WalletResponse(wallet.getId(), wallet.getCustomer().getId(), wallet.getBranch().getId(),
				wallet.isActive());
	}

	private List<WalletResponse> mapList(List<Wallet> wallets) {

		List<WalletResponse> responses = new ArrayList<>();

		for (Wallet b : wallets) {
			responses.add(mapToResponse(b));
		}

		return responses;
	}

//	public WalletResponse create(WalletRequest request) {
//
//		Customer customer = customerRepository.findById(request.getCustomerId())
//				.orElseThrow(() -> new RuntimeException("Customer not found"));
//
//		Branch branch = branchRepository.findById(request.getBranchId())
//				.orElseThrow(() -> new RuntimeException("Branch not found"));
//
//		if (!branch.isActive()) {
//			throw new RuntimeException("Branch is inactive");
//		}
//
//		if (!branch.getCenter().isActive()) {
//			throw new RuntimeException("Center is inactive");
//		}
//
//		Wallet wallet = new Wallet();
//		wallet.setCustomer(customer);
//		wallet.setBranch(branch);
//
//		Wallet saved = repository.save(wallet);
//
//		return mapToResponse(saved);
//	}

	public WalletResponse create(WalletRequest request) {

		Branch branch = validate(request);

		Customer customer = customerRepository.findById(request.getCustomerId())
				.orElseThrow(() -> new NotFoundException("Customer not found"));

		Wallet wallet = new Wallet();
		wallet.setCustomer(customer);
		wallet.setBranch(branch);

		Wallet saved = repository.save(wallet);

//		walletAssetService.seedWalletAssets(saved);

		return mapToResponse(saved);
	}

	public List<WalletResponse> listAll() {

		List<Wallet> wallets = repository.findAll();
		List<WalletResponse> response = new ArrayList<>();

		for (Wallet wallet : wallets) {
			response.add(mapToResponse(wallet));
		}

		return response;
	}

	public List<WalletResponse> listActive() {

		List<Wallet> wallets = repository.findByActiveTrue();
		List<WalletResponse> response = new ArrayList<>();

		for (Wallet wallet : wallets) {
			response.add(mapToResponse(wallet));
		}

		return response;
	}

	public List<WalletResponse> list() {

		Employee current = SecurityUtils.getCurrentEmployee();

		List<Wallet> wallets;

		if (current.getRole() == Role.ORG_ADMIN) {

			wallets = repository.findAll();

		} else if (current.getRole() == Role.CENTER_ADMIN || current.getRole() == Role.CENTER_OPERATOR) {

			Long centerId = SecurityUtils.getCurrentCenterId();

			if (centerId == null) {
				throw new NotFoundException("Center not found in session");
			}

			wallets = repository.findByBranchCenterId(centerId);

		} else if (current.getRole() == Role.BRANCH_ADMIN || current.getRole() == Role.BRANCH_OPERATOR) {

			Long branchId = SecurityUtils.getCurrentBranchId();

			if (branchId == null) {
				throw new NotFoundException("Branch not found in session");
			}

			wallets = repository.findByBranchId(branchId);

		} else {
			throw new ForbiddenException("Unauthorized");
		}

		return mapList(wallets);
	}

	public List<WalletResponse> listByCustomer(Long customerId) {

		List<Wallet> wallets = repository.findByCustomerId(customerId);
		List<WalletResponse> response = new ArrayList<>();

		for (Wallet wallet : wallets) {
			response.add(mapToResponse(wallet));
		}

		return response;
	}

	@Transactional
	public WalletResponse deactivate(Long walletId) {

		Wallet wallet = repository.findById(walletId).orElseThrow(() -> new NotFoundException("Wallet not found"));

		if (!wallet.isActive()) {
			throw new BusinessException("Wallet already inactive");
		}

		wallet.setActive(false);

		return mapToResponse(wallet);
	}

	@Transactional
	public WalletResponse reactivate(Long walletId) {

		Wallet wallet = repository.findById(walletId).orElseThrow(() -> new NotFoundException("Wallet not found"));

		if (wallet.isActive()) {
			throw new BusinessException("Wallet already active");
		}

		if (!wallet.getBranch().isActive()) {
			throw new BusinessException("Branch inactive");
		}

		if (!wallet.getBranch().getCenter().isActive()) {
			throw new BusinessException("Center inactive");
		}

		wallet.setActive(true);

		return mapToResponse(wallet);
	}

	private Branch validate(WalletRequest request) {

		Branch branch = branchRepository.findById(request.getBranchId())
				.orElseThrow(() -> new NotFoundException("Branch not found"));

		validateScope(branch);
		validateBranchUsable(branch);

		return branch;
	}

	private void validateBranchExists(Long branchId) {

		if (!branchRepository.existsById(branchId)) {
			throw new NotFoundException("Branch not found");
		}
	}

	private void validateBranchUsable(Branch branch) {

		if (!branch.isActive()) {
			throw new BusinessException("Branch is inactive");
		}

		if (!branch.getCenter().isActive()) {
			throw new BusinessException("Center is inactive");
		}
	}

	private void validateScope(Branch branch) {

		Employee current = SecurityUtils.getCurrentEmployee();

		if (current.getRole() == Role.ORG_ADMIN) {
			return;
		}

		if (current.getRole() == Role.CENTER_ADMIN || current.getRole() == Role.CENTER_OPERATOR) {

			Long centerId = SecurityUtils.getCurrentCenterId();

			if (centerId == null) {
				throw new NotFoundException("Center not found in session");
			}

			if (!branch.getCenter().getId().equals(centerId)) {
				throw new BusinessException("Cannot create wallet for another center");
			}

			return;
		}

		if (current.getRole() == Role.BRANCH_ADMIN || current.getRole() == Role.BRANCH_OPERATOR) {

			Long currentBranchId = SecurityUtils.getCurrentBranchId();

			if (currentBranchId == null) {
				throw new NotFoundException("Branch not found in session");
			}

			if (!branch.getId().equals(currentBranchId)) {
				throw new BusinessException("Cannot create wallet for another branch");
			}

			return;
		}

		throw new ForbiddenException("Unauthorized");
	}
	// bunu asset service'e tasidim.
//	private void validateWalletScope(Wallet wallet) {
//
//		Employee current = SecurityUtils.getCurrentEmployee();
//
//		if (current.getRole() == Role.ORG_ADMIN) {
//			return;
//		}
//
//		if (current.getRole() == Role.CENTER_ADMIN || current.getRole() == Role.CENTER_OPERATOR) {
//			Long centerId = SecurityUtils.getCurrentCenterId();
//
//			if (!wallet.getBranch().getCenter().getId().equals(centerId)) {
//				throw new RuntimeException("Cannot access wallet from another center");
//			}
//
//			return;
//		}
//
//		if (current.getRole() == Role.BRANCH_ADMIN || current.getRole() == Role.BRANCH_OPERATOR) {
//			Long branchId = SecurityUtils.getCurrentBranchId();
//
//			if (!wallet.getBranch().getId().equals(branchId)) {
//				throw new RuntimeException("Cannot access wallet from another branch");
//			}
//
//			return;
//		}
//
//		throw new RuntimeException("Unauthorized");
//	}

}