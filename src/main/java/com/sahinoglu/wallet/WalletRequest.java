package com.sahinoglu.wallet;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WalletRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private Long branchId;
}