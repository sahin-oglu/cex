package com.sahinoglu.wallet;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sahinoglu.coin.Coin;

public interface WalletAssetRepository extends JpaRepository<WalletAsset, Long> {
	Optional<WalletAsset> findByWalletAndCoin(Wallet wallet, Coin coin);

}
