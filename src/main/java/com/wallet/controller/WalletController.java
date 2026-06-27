package com.wallet.controller;

import com.wallet.dto.response.WalletResponseDTO;
import com.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/saldo")
    public ResponseEntity<WalletResponseDTO> getSaldo(){
        return ResponseEntity.ok(walletService.getWalletByUserEmail());
    }
}
