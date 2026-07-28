package com.sahinoglu.wallet;

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
import com.sahinoglu.exception.BusinessException;
import com.sahinoglu.exception.ForbiddenException;
import com.sahinoglu.exception.NotFoundException;
import com.sahinoglu.security.ScopeGuard;
import com.sahinoglu.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class WalletService {

	private final WalletRepository repository;
	private final CustomerRepository customerRepository;
	private final BranchRepository branchRepository;
	private final ScopeGuard scopeGuard;

	private final SecurityUtils securityUtils;

	public WalletResponse create(WalletRequest request) {

		Branch branch = validate(request);

		Customer customer = customerRepository.findById(request.getCustomerId())
				.orElseThrow(() -> new NotFoundException("Customer not found"));

		Wallet wallet = new Wallet();
		wallet.setCustomer(customer);
		wallet.setBranch(branch);

		Wallet saved = repository.save(wallet);

		return mapToResponse(saved);
	}

	public List<WalletResponse> list() {

		Employee current = securityUtils.getCurrentEmployee();

		List<Wallet> wallets;

		if (current.getRole() == Role.ORG_ADMIN) {

			wallets = repository.findAll();

		} else if (current.getRole() == Role.CENTER_ADMIN || current.getRole() == Role.CENTER_OPERATOR) {

			wallets = repository.findByBranchCenterId(scopeGuard.requireCurrentCenterId());

		} else if (current.getRole() == Role.BRANCH_ADMIN || current.getRole() == Role.BRANCH_OPERATOR) {

			wallets = repository.findByBranchId(scopeGuard.requireCurrentBranchId());

		} else {
			throw new ForbiddenException("Unauthorized");
		}

		return mapList(wallets);
	}

	public List<WalletResponse> listByCustomer(Long customerId) {

		Employee current = securityUtils.getCurrentEmployee();

		List<Wallet> wallets;

		if (current.getRole() == Role.ORG_ADMIN) {

			wallets = repository.findByCustomerId(customerId);

		} else if (current.getRole() == Role.CENTER_ADMIN || current.getRole() == Role.CENTER_OPERATOR) {

			wallets = repository.findByCustomerIdAndBranchCenterId(customerId, scopeGuard.requireCurrentCenterId());

		} else if (current.getRole() == Role.BRANCH_ADMIN || current.getRole() == Role.BRANCH_OPERATOR) {

			wallets = repository.findByCustomerIdAndBranchId(customerId, scopeGuard.requireCurrentBranchId());

		} else {
			throw new ForbiddenException("Unauthorized");
		}

		return mapList(wallets);
	}

	@Transactional
	public WalletResponse deactivate(Long walletId) {

		Wallet wallet = repository.findById(walletId).orElseThrow(() -> new NotFoundException("Wallet not found"));

		validateWalletScope(wallet);

		if (!wallet.isActive()) {
			throw new BusinessException("Wallet already inactive");
		}

		wallet.setActive(false);

		return mapToResponse(wallet);
	}

	@Transactional
	public WalletResponse reactivate(Long walletId) {

		Wallet wallet = repository.findById(walletId).orElseThrow(() -> new NotFoundException("Wallet not found"));

		validateWalletScope(wallet);

		if (wallet.isActive()) {
			throw new BusinessException("Wallet already active");
		}

		validateBranchUsable(wallet.getBranch());

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

	private void validateBranchUsable(Branch branch) {

		if (!branch.isActive()) {
			throw new BusinessException("Branch is inactive");
		}

		if (!branch.getCenter().isActive()) {
			throw new BusinessException("Center is inactive");
		}
	}

	private void validateScope(Branch branch) {
		scopeGuard.requireBranchOwnership(branch.getId(), branch.getCenter().getId());
	}

	private void validateWalletScope(Wallet wallet) {
		scopeGuard.requireBranchOwnership(wallet.getBranch().getId(), wallet.getBranch().getCenter().getId());
	}

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
}