package com.sahinoglu.wallet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sahinoglu.branch.Branch;
import com.sahinoglu.branch.BranchRepository;
import com.sahinoglu.customer.Customer;
import com.sahinoglu.customer.CustomerRepository;
import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.Role;
import com.sahinoglu.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletService {

	private final WalletRepository repository;
	private final CustomerRepository customerRepository;
	private final BranchRepository branchRepository;

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

		validate(request);

		Wallet wallet = new Wallet();

//		wallet.setBalance(BigDecimal.ZERO);

		Customer customer = customerRepository.findById(request.getCustomerId())
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		Branch branch = branchRepository.findById(request.getBranchId())
				.orElseThrow(() -> new RuntimeException("Branch not found"));

		wallet.setCustomer(customer);
		wallet.setBranch(branch);

		Wallet saved = repository.save(wallet);

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
				throw new RuntimeException("Center not found in session");
			}

			wallets = repository.findByBranchCenterId(centerId);

		} else if (current.getRole() == Role.BRANCH_ADMIN || current.getRole() == Role.BRANCH_OPERATOR) {

			Long branchId = SecurityUtils.getCurrentBranchId();

			if (branchId == null) {
				throw new RuntimeException("Branch not found in session");
			}

			wallets = repository.findByBranchId(branchId);

		} else {
			throw new RuntimeException("Unauthorized");
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

		Wallet wallet = repository.findById(walletId).orElseThrow(() -> new RuntimeException("Wallet not found"));

		if (!wallet.isActive()) {
			throw new RuntimeException("Wallet already inactive");
		}

		wallet.setActive(false);

		return mapToResponse(wallet);
	}

	@Transactional
	public WalletResponse reactivate(Long walletId) {

		Wallet wallet = repository.findById(walletId).orElseThrow(() -> new RuntimeException("Wallet not found"));

		if (wallet.isActive()) {
			throw new RuntimeException("Wallet already active");
		}

		if (!wallet.getBranch().isActive()) {
			throw new RuntimeException("Branch inactive");
		}

		if (!wallet.getBranch().getCenter().isActive()) {
			throw new RuntimeException("Center inactive");
		}

		wallet.setActive(true);

		return mapToResponse(wallet);
	}

	private void validate(WalletRequest request) {

		validateBranchExists(request.getBranchId());

		validateScope(request.getBranchId());

	}

	private void validateBranchExists(Long branchId) {

		if (!branchRepository.existsById(branchId)) {
			throw new RuntimeException("Branch not found");
		}
	}

	private void validateScope(Long branchId) {

		Employee current = SecurityUtils.getCurrentEmployee();

		Branch branch = branchRepository.findById(branchId).orElseThrow(() -> new RuntimeException("Branch not found"));

		if (current.getRole() == Role.ORG_ADMIN) {
			return;
		}

		if (current.getRole() == Role.CENTER_ADMIN || current.getRole() == Role.CENTER_OPERATOR) {

			Long centerId = SecurityUtils.getCurrentCenterId();

			if (!branch.getCenter().getId().equals(centerId)) {
				throw new RuntimeException("Cannot create wallet for another center");
			}
		}

		if (current.getRole() == Role.BRANCH_ADMIN || current.getRole() == Role.BRANCH_OPERATOR) {

			Long currentBranchId = SecurityUtils.getCurrentBranchId();

			if (!branch.getId().equals(currentBranchId)) {
				throw new RuntimeException("Cannot create wallet for another branch");
			}
		}
	}
}