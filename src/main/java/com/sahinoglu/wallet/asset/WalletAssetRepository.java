package com.sahinoglu.wallet.asset;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sahinoglu.coin.Coin;
import com.sahinoglu.wallet.Wallet;

public interface WalletAssetRepository extends JpaRepository<WalletAsset, Long> {
	Optional<WalletAsset> findByWalletAndCoin(Wallet wallet, Coin coin);

	List<WalletAsset> findByWalletId(Long walletId);
}
