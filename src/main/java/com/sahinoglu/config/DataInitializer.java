package com.sahinoglu.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

@Component
@RequiredArgsConstructor
/**
 * proje ayaga kalktiginda hazir admin olsun. bu projede admin global user
 * oldugu icin her daim bu sekilde olusturulmasi ongorulmektedir. GERI KALAN HER
 * SEY DEVELOPMENT ICIN.
 */
public class DataInitializer implements CommandLineRunner {

	private final WalletAssetRepository walletAssetRepository;

	private final WalletRepository walletRepository;

	private final CustomerRepository customerRepository;
	private final EmployeeRepository employeeRepository;
	//
	private final BranchRepository branchRepository;
	private final CenterRepository centerRepository;
	private final CoinService coinService;
	private final CoinRepository coinRepository;

	@Override
	public void run(String... args) {

		if (employeeRepository.findByUsername("admin").isPresent()) {
			return;
		}

		Employee admin = new Employee();
		admin.setUsername("admin");
		admin.setPassword("admin");
		admin.setFirstName("System");
		admin.setLastName("Admin");
		admin.setRole(Role.ORG_ADMIN);

		// testler esnasinda bop ve cop da olsun.

		Center c = new Center();
		c.setName("istanbulCenter");
		c.setLocation("istanbul");
		Employee cop = new Employee();
		cop.setUsername("cop");
		cop.setPassword("cop");
		cop.setFirstName("cg");
		cop.setLastName("yilmaz");
		cop.setCenter(c);
		cop.setRole(Role.CENTER_OPERATOR);
		Branch b = new Branch();
		b.setName("fatihBranch");
		b.setLocation("fatih");
		b.setCenter(c);
		Employee bop = new Employee();
		bop.setUsername("bop");
		bop.setPassword("bop");
		bop.setFirstName("ib");
		bop.setLastName("tuncer");
		bop.setCenter(c);
		bop.setBranch(b);
		bop.setRole(Role.BRANCH_OPERATOR);
		Customer cus1 = new Customer();
		cus1.setName("musteri1");
		cus1.setPhone("05444444444");
		Wallet w1 = new Wallet();
		w1.setCustomer(cus1);
		w1.setBranch(b);
		coinService.syncCoins();
		Coin btc = coinRepository.findById("bitcoin").orElseThrow(() -> new NotFoundException("Bitcoin not found"));
		WalletAsset wa1 = new WalletAsset();
		wa1.setWallet(w1);
		wa1.setAmount(new BigDecimal("0.1"));
		wa1.setCoin(btc);
		Customer cus2 = new Customer();
		cus2.setName("musteri2");
		cus2.setPhone("05333333333");
		Wallet w2 = new Wallet();
		w2.setCustomer(cus2);
		w2.setBranch(b);
		WalletAsset wa2 = new WalletAsset();
		wa2.setWallet(w2);
		wa2.setAmount(new BigDecimal("0.1"));
		wa2.setCoin(btc);

		///////////////////

		employeeRepository.save(admin);

		//////////////////
		centerRepository.save(c);
		employeeRepository.save(cop);
		branchRepository.save(b);
		employeeRepository.save(bop);
		customerRepository.save(cus1);
		customerRepository.save(cus2);
		walletRepository.save(w1);
		walletRepository.save(w2);
		walletAssetRepository.save(wa1);
		walletAssetRepository.save(wa2);

	}
}