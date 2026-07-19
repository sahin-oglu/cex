package com.sahinoglu.wallet;

public record WalletResponse(Long id, Long customerId, Long branchId, boolean active) {
}
