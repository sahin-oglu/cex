package com.sahinoglu.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.sahinoglu.coin.Coin;
import com.sahinoglu.coin.CoinRepository;
import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.Role;
import com.sahinoglu.security.SecurityUtils;
import com.sahinoglu.wallet.Wallet;
import com.sahinoglu.wallet.WalletAsset;
import com.sahinoglu.wallet.WalletAssetRepository;
import com.sahinoglu.wallet.WalletRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
/**
 * hayatimda yazdigim en karmasik class. validation method'lari en asagida.
 * belki de validation diye bir class acmaliydim ama ilk basta bu kadar
 * karmasiklasacagini bilemedim.
 */
public class TransactionRequestService {

	private final TransactionRequestRepository requestRepository;
	private final TransactionRepository transactionRepository;
	private final WalletRepository walletRepository;
	private final CoinRepository coinRepository;
	private final WalletAssetRepository walletAssetRepository;

	private TransactionRequestResponse mapToResponse(TransactionRequest tr) {
		// NPE yememek icin. cunku review'lanmamis request'lerin bu field'lari null
		// olacaktir.
		Long reviewedById = tr.getReviewedBy() != null ? tr.getReviewedBy().getId() : null;
		LocalDateTime reviewedAt = tr.getReviewedAt() != null ? tr.getReviewedAt() : null;

		return new TransactionRequestResponse(tr.getId(), tr.getAmount(), tr.getFromWallet().getId(),
				tr.getToWallet().getId(), tr.getCoin().getId(), tr.getCoin().getSymbol(), tr.getStatus(),
				tr.getRequestedBy().getId(), reviewedById, tr.getRequestedAt(), reviewedAt);
	}

	public TransactionRequestResponse createTransactionRequest(TransactionRequestRequest request) {

		Employee current = SecurityUtils.getCurrentEmployee();

		validateRequestBasics(request, current);

		Wallet fromWallet = walletRepository.findById(request.getFromWalletId())
				.orElseThrow(() -> new RuntimeException("From wallet not found"));

		Wallet toWallet = walletRepository.findById(request.getToWalletId())
				.orElseThrow(() -> new RuntimeException("To wallet not found"));

		Coin coin = coinRepository.findById(request.getCoinId())
				.orElseThrow(() -> new RuntimeException("Coin not found"));

		validateScope(current, fromWallet);
		validateBalance(fromWallet, coin, request.getAmount());

		TransactionRequest tr = buildTransactionRequest(request, current, fromWallet, toWallet, coin);

		TransactionRequest saved = requestRepository.save(tr);

		return mapToResponse(saved);
	}

	private TransactionRequest buildTransactionRequest(TransactionRequestRequest request, Employee current,
			Wallet fromWallet, Wallet toWallet, Coin coin) {

		TransactionRequest tr = new TransactionRequest();

		tr.setFromWallet(fromWallet);
		tr.setToWallet(toWallet);
		tr.setCoin(coin);
		tr.setAmount(request.getAmount());

		tr.setRequestedBy(current);
		tr.setRequestedAt(LocalDateTime.now());

		tr.setStatus(TransactionRequestStatus.PENDING);

		return tr;
	}

	@Transactional
	public TransactionRequestResponse approveTransactionRequest(Long requestId) {

		Employee current = SecurityUtils.getCurrentEmployee();

		validateApprovalRole(current);

		TransactionRequest tr = requestRepository.findById(requestId)
				.orElseThrow(() -> new RuntimeException("Request not found"));

		validatePending(tr);
		validateApprovalScope(current, tr);

		Wallet fromWallet = tr.getFromWallet();
		Wallet toWallet = tr.getToWallet();
		Coin coin = tr.getCoin();
		BigDecimal amount = tr.getAmount();

		WalletAsset fromAsset = walletAssetRepository.findByWalletAndCoin(fromWallet, coin)
				.orElseThrow(() -> new RuntimeException("No asset for this coin"));

		if (fromAsset.getAmount().compareTo(amount) < 0) {
			throw new RuntimeException("Insufficient balance");
		}

		fromAsset.setAmount(fromAsset.getAmount().subtract(amount));

		WalletAsset toAsset = walletAssetRepository.findByWalletAndCoin(toWallet, coin).orElse(null);

		if (toAsset == null) {
			toAsset = new WalletAsset();
			toAsset.setWallet(toWallet);
			toAsset.setCoin(coin);
			toAsset.setAmount(BigDecimal.ZERO);
		}

		toAsset.setAmount(toAsset.getAmount().add(amount));

		walletAssetRepository.save(toAsset);

		Transaction tx = new Transaction();

		tx.setFromWallet(fromWallet);
		tx.setToWallet(toWallet);
		tx.setCoin(coin);
		tx.setAmount(amount);
		tx.setExecutedAt(LocalDateTime.now());
		tx.setRequest(tr);

		tx.setPriceAtExecution(coin.getPrice());

		transactionRepository.save(tx);

		tr.setStatus(TransactionRequestStatus.APPROVED);
		tr.setReviewedBy(current);
		tr.setReviewedAt(LocalDateTime.now());

		return mapToResponse(tr);
	}

	@Transactional
	public TransactionRequestResponse rejectTransactionRequest(Long requestId) {

		Employee current = SecurityUtils.getCurrentEmployee();

		validateApprovalRole(current);

		TransactionRequest tr = requestRepository.findById(requestId)
				.orElseThrow(() -> new RuntimeException("Request not found"));

		validatePending(tr);
		validateApprovalScope(current, tr);

		tr.setStatus(TransactionRequestStatus.REJECTED);
		tr.setReviewedBy(current);
		tr.setReviewedAt(LocalDateTime.now());

		return mapToResponse(tr);
	}

	private void validateApprovalRole(Employee current) {

		if (current.getRole() != Role.CENTER_OPERATOR) {
			throw new RuntimeException("Only a center operator can approve");
		}
	}

	private void validatePending(TransactionRequest tr) {

		if (tr.getStatus() != TransactionRequestStatus.PENDING) {
			throw new RuntimeException("Request already processed");
		}
	}

	private void validateApprovalScope(Employee current, TransactionRequest tr) {

		Long centerId = SecurityUtils.getCurrentCenterId();

		if (centerId == null) {
			throw new RuntimeException("Center not found in session");
		}

		if (!tr.getFromWallet().getBranch().getCenter().getId().equals(centerId)) {
			throw new RuntimeException("Cannot approve request from another center");
		}
	}

	private void validateScope(Employee current, Wallet fromWallet) {

		Long currentBranchId = SecurityUtils.getCurrentBranchId();

		if (currentBranchId == null) {
			throw new RuntimeException("Branch not found in session");
		}

		if (!fromWallet.getBranch().getId().equals(currentBranchId)) {
			throw new RuntimeException("Cannot use wallet from another branch");
		}
	}

	private void validateBalance(Wallet fromWallet, Coin coin, BigDecimal amount) {

		WalletAsset asset = walletAssetRepository.findByWalletAndCoin(fromWallet, coin)
				.orElseThrow(() -> new RuntimeException("No asset for this coin"));

		if (asset.getAmount().compareTo(amount) < 0) {
			throw new RuntimeException("Insufficient balance");
		}
	}

	private void validateRequestBasics(TransactionRequestRequest request, Employee current) {

		if (current.getRole() != Role.BRANCH_OPERATOR) {
			throw new RuntimeException("Only branch operator can create transaction request");
		}

		if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new RuntimeException("Amount must be greater than zero");
		}

		if (request.getFromWalletId().equals(request.getToWalletId())) {
			throw new RuntimeException("Cannot transfer to same wallet");
		}

		if (request.getCoinId() == null) {
			throw new RuntimeException("Coin is required");
		}
	}

}
