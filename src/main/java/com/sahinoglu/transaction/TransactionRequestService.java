package com.sahinoglu.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import com.sahinoglu.wallet.asset.WalletAsset;
import com.sahinoglu.wallet.asset.WalletAssetRepository;

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
				.orElseThrow(() -> new NotFoundException("From wallet not found"));

		Wallet toWallet = walletRepository.findById(request.getToWalletId())
				.orElseThrow(() -> new NotFoundException("To wallet not found"));

		Coin coin = coinRepository.findById(request.getCoinId())
				.orElseThrow(() -> new NotFoundException("Coin not found"));
		validateWalletsAreUsable(fromWallet, toWallet);
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
				.orElseThrow(() -> new NotFoundException("Request not found"));

		validatePending(tr);
		validateApprovalScope(current, tr);

		Wallet fromWallet = tr.getFromWallet();
		Wallet toWallet = tr.getToWallet();
		Coin coin = tr.getCoin();
		BigDecimal amount = tr.getAmount();

		validateWalletsAreUsable(fromWallet, toWallet);
		validateCoinPriceAvailable(coin);

		WalletAsset fromAsset = debitFromWallet(fromWallet, coin, amount);
		WalletAsset toAsset = creditToWallet(toWallet, coin, amount);

		walletAssetRepository.save(fromAsset);
		walletAssetRepository.save(toAsset);

		LocalDateTime now = LocalDateTime.now();

		tr.setStatus(TransactionRequestStatus.APPROVED);
		tr.setReviewedBy(current);
		tr.setReviewedAt(now);

		Transaction tx = buildTransaction(tr, fromWallet, toWallet, coin, amount, now);
		transactionRepository.save(tx);

		return mapToResponse(tr);
	}

	@Transactional
	public TransactionRequestResponse rejectTransactionRequest(Long requestId) {

		Employee current = SecurityUtils.getCurrentEmployee();

		validateApprovalRole(current);

		TransactionRequest tr = requestRepository.findById(requestId)
				.orElseThrow(() -> new NotFoundException("Request not found"));

		validatePending(tr);
		validateApprovalScope(current, tr);

		tr.setStatus(TransactionRequestStatus.REJECTED);
		tr.setReviewedBy(current);
		tr.setReviewedAt(LocalDateTime.now());

		return mapToResponse(tr);
	}

	public List<TransactionRequestResponse> list() {

		Employee current = SecurityUtils.getCurrentEmployee();

		List<TransactionRequest> requests;

		if (current.getRole() == Role.ORG_ADMIN) {

			requests = requestRepository.findAll();

		} else if (current.getRole() == Role.CENTER_ADMIN || current.getRole() == Role.CENTER_OPERATOR) {

			Long centerId = SecurityUtils.getCurrentCenterId();

			if (centerId == null) {
				throw new ForbiddenException("Current user is not assigned to a center");
			}

			requests = requestRepository.findByFromWalletBranchCenterId(centerId);

		} else if (current.getRole() == Role.BRANCH_ADMIN) {

			Long branchId = SecurityUtils.getCurrentBranchId();

			if (branchId == null) {
				throw new ForbiddenException("Current user is not assigned to a branch");
			}

			requests = requestRepository.findByFromWalletBranchId(branchId);

		} else if (current.getRole() == Role.BRANCH_OPERATOR) {

			requests = requestRepository.findByRequestedById(current.getId());

		} else {
			throw new ForbiddenException("Unauthorized");
		}

		return requests.stream().map(this::mapToResponse).toList();
	}

	private void validateApprovalRole(Employee current) {

		if (current.getRole() != Role.CENTER_OPERATOR) {
			throw new BusinessException("Only a center operator can approve");
		}
	}

	private void validatePending(TransactionRequest tr) {

		if (tr.getStatus() != TransactionRequestStatus.PENDING) {
			throw new BusinessException("Request already processed");
		}
	}

	private void validateApprovalScope(Employee current, TransactionRequest tr) {

		Long centerId = SecurityUtils.getCurrentCenterId();

		if (centerId == null) {
			throw new NotFoundException("Center not found in session");
		}

		if (!tr.getFromWallet().getBranch().getCenter().getId().equals(centerId)) {
			throw new BusinessException("Cannot approve request from another center");
		}
	}

	private void validateScope(Employee current, Wallet fromWallet) {

		Long currentBranchId = SecurityUtils.getCurrentBranchId();

		if (currentBranchId == null) {
			throw new NotFoundException("Branch not found in session");
		}

		if (!fromWallet.getBranch().getId().equals(currentBranchId)) {
			throw new BusinessException("Cannot use wallet from another branch");
		}
	}

	private void validateBalance(Wallet fromWallet, Coin coin, BigDecimal amount) {

		WalletAsset asset = walletAssetRepository.findByWalletAndCoin(fromWallet, coin)
				.orElseThrow(() -> new NotFoundException("No asset for this coin"));

		if (asset.getAmount().compareTo(amount) < 0) {
			throw new BusinessException("Insufficient balance for selected asset");
		}
	}

	private void validateRequestBasics(TransactionRequestRequest request, Employee current) {
		if (request.getFromWalletId() == null || request.getToWalletId() == null) {
			throw new BusinessException("Wallet ids are required");
		}

		if (request.getFromWalletId().equals(request.getToWalletId())) {
			throw new BusinessException("Cannot transfer to same wallet");
		}
		if (current.getRole() != Role.BRANCH_OPERATOR) {
			throw new BusinessException("Only branch operator can create transaction request");
		}

		if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new BusinessException("Amount must be greater than zero");
		}

		if (request.getFromWalletId().equals(request.getToWalletId())) {
			throw new BusinessException("Cannot transfer to same wallet");
		}
		// bunu boyle yapinca ne fark ediyor bak
		if (request.getCoinId() == null) {
			throw new BusinessException("Coin is required");
		}
	}

	private void validateWalletsAreUsable(Wallet fromWallet, Wallet toWallet) {

		if (!fromWallet.isActive()) {
			throw new BusinessException("From wallet is inactive");
		}

		if (!toWallet.isActive()) {
			throw new BusinessException("To wallet is inactive");
		}

		if (!fromWallet.getBranch().isActive() || !fromWallet.getBranch().getCenter().isActive()) {
			throw new BusinessException("From wallet branch or center is inactive");
		}

		if (!toWallet.getBranch().isActive() || !toWallet.getBranch().getCenter().isActive()) {
			throw new BusinessException("To wallet branch or center is inactive");
		}
	}

	private void validateCoinPriceAvailable(Coin coin) {
		if (coin.getPrice() == null) {
			throw new BusinessException("Coin price is not available");
		}
	}

	private WalletAsset debitFromWallet(Wallet fromWallet, Coin coin, BigDecimal amount) {

		WalletAsset fromAsset = walletAssetRepository.findByWalletAndCoin(fromWallet, coin)
				.orElseThrow(() -> new BusinessException("No asset for this coin"));

		if (fromAsset.getAmount().compareTo(amount) < 0) {
			throw new BusinessException("Insufficient balance");
		}

		fromAsset.setAmount(fromAsset.getAmount().subtract(amount));

		return fromAsset;
	}

	private WalletAsset creditToWallet(Wallet toWallet, Coin coin, BigDecimal amount) {

		WalletAsset toAsset = walletAssetRepository.findByWalletAndCoin(toWallet, coin).orElse(null);

		if (toAsset == null) {
			toAsset = new WalletAsset();
			toAsset.setWallet(toWallet);
			toAsset.setCoin(coin);
			toAsset.setAmount(BigDecimal.ZERO);
		}

		toAsset.setAmount(toAsset.getAmount().add(amount));

		return toAsset;
	}

	private Transaction buildTransaction(TransactionRequest request, Wallet fromWallet, Wallet toWallet, Coin coin,
			BigDecimal amount, LocalDateTime executedAt) {

		Transaction transaction = new Transaction();

		transaction.setFromWallet(fromWallet);
		transaction.setToWallet(toWallet);
		transaction.setCoin(coin);

		transaction.setAmount(amount);
		transaction.setExecutedAt(executedAt);
		transaction.setRequest(request);
		transaction.setPriceAtExecution(coin.getPrice());

		Employee requestedBy = request.getRequestedBy();
		Employee reviewedBy = request.getReviewedBy();

		transaction.setRequestedById(requestedBy.getId());
		transaction.setRequestedByUsername(requestedBy.getUsername());

		transaction.setReviewedById(reviewedBy.getId());
		transaction.setReviewedByUsername(reviewedBy.getUsername());

		return transaction;
	}

}
