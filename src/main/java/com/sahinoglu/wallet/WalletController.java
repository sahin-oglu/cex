package com.sahinoglu.wallet;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService service;

    @GetMapping("/wallets")
    public List<WalletResponse> list(@RequestParam(name = "active", required = false) Boolean active) {

        if (active != null && active) {
            return service.listActive();
        }

        return service.listAll();
    }

    @GetMapping("/wallets/customer/{customerId}")
    public List<WalletResponse> listByCustomer(@PathVariable Long customerId) {

        return service.listByCustomer(customerId);
    }

    @PostMapping("/admin/wallets")
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse create(@Valid @RequestBody WalletRequest request) {

        return service.create(request);
    }

    @PatchMapping("/admin/wallets/{id}/deactivate")
    public WalletResponse deactivate(@PathVariable Long id) {

        return service.deactivate(id);
    }

    @PatchMapping("/admin/wallets/{id}/reactivate")
    public WalletResponse reactivate(@PathVariable Long id) {

        return service.reactivate(id);
    }
}