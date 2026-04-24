package com.sahinoglu.wallet;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponse {

    private Long id;

    private Long customerId;

    private Long branchId;


    private boolean active;
}