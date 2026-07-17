package com.sahinoglu.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sahinoglu.branch.Branch;
import com.sahinoglu.branch.BranchRepository;
import com.sahinoglu.center.Center;
import com.sahinoglu.center.CenterRepository;
import com.sahinoglu.coin.Coin;
import com.sahinoglu.coin.CoinRepository;
import com.sahinoglu.coin.CoinService;
import com.sahinoglu.customer.Customer;
import com.sahinoglu.customer.CustomerRepository;
import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.EmployeeRepository;
import com.sahinoglu.employee.Role;
import com.sahinoglu.exception.NotFoundException;
import com.sahinoglu.wallet.Wallet;
import com.sahinoglu.wallet.WalletRepository;
import com.sahinoglu.wallet.asset.WalletAsset;
import com.sahinoglu.wallet.asset.WalletAssetRepository;

import lombok.RequiredArgsConstructor;

/**
 * Creates a complete demo scenario when demo data is enabled.
 *
 * Configure using: app.demo-data.enabled=true
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.demo-data.enabled", havingValue = "true")
public class DemoDataInitializer implements CommandLineRunner {

	private static final String CENTER_ADMIN_USERNAME = "cadmin";
	private static final String CENTER_ADMIN_PASSWORD = "cadmin";

	private static final String CENTER_OPERATOR_USERNAME = "cop";
	private static final String CENTER_OPERATOR_PASSWORD = "cop";

	private static final String BRANCH_ADMIN_USERNAME = "badmin";
	private static final String BRANCH_ADMIN_PASSWORD = "badmin";

	private static final String BRANCH_OPERATOR_USERNAME = "bop";
	private static final String BRANCH_OPERATOR_PASSWORD = "bop";

	private final WalletAssetRepository walletAssetRepository;
	private final WalletRepository walletRepository;
	private final CustomerRepository customerRepository;
	private final EmployeeRepository employeeRepository;
	private final BranchRepository branchRepository;
	private final CenterRepository centerRepository;
	private final CoinService coinService;
	private final CoinRepository coinRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void run(String... args) {
		if (demoDataAlreadyInitialized()) {
			createMissingDemoAdmins();
			return;
		}

		createDemoData();
	}

	private boolean demoDataAlreadyInitialized() {
		return employeeRepository.findByUsername(CENTER_OPERATOR_USERNAME).isPresent();
	}

	/**
	 * Adds the new admin demo accounts to an existing demo database without
	 * recreating centers, branches, customers or wallets.
	 */
	private void createMissingDemoAdmins() {
		Employee centerOperator = employeeRepository.findByUsername(CENTER_OPERATOR_USERNAME)
				.orElseThrow(() -> new NotFoundException("Demo center operator not found"));

		Employee branchOperator = employeeRepository.findByUsername(BRANCH_OPERATOR_USERNAME)
				.orElseThrow(() -> new NotFoundException("Demo branch operator not found"));

		Center center = centerOperator.getCenter();
		Branch branch = branchOperator.getBranch();

		createCenterAdminIfMissing(center);
		createBranchAdminIfMissing(center, branch);
	}

	private void createDemoData() {
		Center center = createDemoCenter();
		Branch branch = createDemoBranch(center);

		createCenterAdminIfMissing(center);
		createCenterOperator(center);

		createBranchAdminIfMissing(center, branch);
		createBranchOperator(center, branch);

		Coin bitcoin = getBitcoin();

		createDemoCustomer("musteri1", "05444444444", branch, bitcoin);

		createDemoCustomer("musteri2", "05333333333", branch, bitcoin);
	}

	private Center createDemoCenter() {
		Center center = new Center();
		center.setName("istanbulCenter");
		center.setLocation("istanbul");

		return centerRepository.save(center);
	}

	private Branch createDemoBranch(Center center) {
		Branch branch = new Branch();
		branch.setName("fatihBranch");
		branch.setLocation("fatih");
		branch.setCenter(center);

		return branchRepository.save(branch);
	}

	private void createCenterAdminIfMissing(Center center) {
		if (employeeRepository.findByUsername(CENTER_ADMIN_USERNAME).isPresent()) {
			return;
		}

		Employee centerAdmin = new Employee();
		centerAdmin.setUsername(CENTER_ADMIN_USERNAME);
		centerAdmin.setPassword(passwordEncoder.encode(CENTER_ADMIN_PASSWORD));
		centerAdmin.setFirstName("Center");
		centerAdmin.setLastName("Admin");
		centerAdmin.setCenter(center);
		centerAdmin.setRole(Role.CENTER_ADMIN);

		employeeRepository.save(centerAdmin);
	}

	private void createCenterOperator(Center center) {
		Employee centerOperator = new Employee();
		centerOperator.setUsername(CENTER_OPERATOR_USERNAME);
		centerOperator.setPassword(passwordEncoder.encode(CENTER_OPERATOR_PASSWORD));
		centerOperator.setFirstName("Center");
		centerOperator.setLastName("Operator");
		centerOperator.setCenter(center);
		centerOperator.setRole(Role.CENTER_OPERATOR);

		employeeRepository.save(centerOperator);
	}

	private void createBranchAdminIfMissing(Center center, Branch branch) {
		if (employeeRepository.findByUsername(BRANCH_ADMIN_USERNAME).isPresent()) {
			return;
		}

		Employee branchAdmin = new Employee();
		branchAdmin.setUsername(BRANCH_ADMIN_USERNAME);
		branchAdmin.setPassword(passwordEncoder.encode(BRANCH_ADMIN_PASSWORD));
		branchAdmin.setFirstName("Branch");
		branchAdmin.setLastName("Admin");
		branchAdmin.setCenter(center);
		branchAdmin.setBranch(branch);
		branchAdmin.setRole(Role.BRANCH_ADMIN);

		employeeRepository.save(branchAdmin);
	}

	private void createBranchOperator(Center center, Branch branch) {
		Employee branchOperator = new Employee();
		branchOperator.setUsername(BRANCH_OPERATOR_USERNAME);
		branchOperator.setPassword(passwordEncoder.encode(BRANCH_OPERATOR_PASSWORD));
		branchOperator.setFirstName("Branch");
		branchOperator.setLastName("Operator");
		branchOperator.setCenter(center);
		branchOperator.setBranch(branch);
		branchOperator.setRole(Role.BRANCH_OPERATOR);

		employeeRepository.save(branchOperator);
	}

	private Coin getBitcoin() {
		if (coinRepository.findById("bitcoin").isEmpty()) {
			coinService.syncCoins();
		}

		return coinRepository.findById("bitcoin").orElseThrow(() -> new NotFoundException("Bitcoin not found"));
	}

	private void createDemoCustomer(String name, String phone, Branch branch, Coin bitcoin) {
		Customer customer = new Customer();
		customer.setName(name);
		customer.setPhone(phone);

		customerRepository.save(customer);

		Wallet wallet = new Wallet();
		wallet.setCustomer(customer);
		wallet.setBranch(branch);

		walletRepository.save(wallet);

		WalletAsset walletAsset = new WalletAsset();
		walletAsset.setWallet(wallet);
		walletAsset.setCoin(bitcoin);
		walletAsset.setAmount(new BigDecimal("0.1"));

		walletAssetRepository.save(walletAsset);
	}
}