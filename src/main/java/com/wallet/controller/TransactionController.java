package com.wallet.controller;

import com.wallet.dto.request.*;
import com.wallet.dto.response.CargoResponseApiDTO;
import com.wallet.dto.response.TransactionResponseDTO;
import com.wallet.dto.response.WalletResponseDTO;
import com.wallet.entity.User;
import com.wallet.service.TransactionService;
import com.wallet.service.impl.ExternalApiGateway;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final ExternalApiGateway externalApiGateway;

    @PostMapping("/deposit")
    public ResponseEntity<WalletResponseDTO> deposit(@Valid @RequestBody DepositRequestDTO depositDTO) {
        WalletResponseDTO walletResponseDTO = transactionService.deposit(depositDTO);
        return ResponseEntity.ok(walletResponseDTO);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(@Valid @RequestBody TransferRequestDTO transferDTO) {
        TransactionResponseDTO transactionResponseDTO = transactionService.transfer(transferDTO);
        return ResponseEntity.ok(transactionResponseDTO);
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactions(@PathVariable String email, @PageableDefault(page = 0, size = 20)Pageable pageable) {
        Page<TransactionResponseDTO> transactions = transactionService.transactions(pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}/{email}")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable UUID id, @PathVariable String email) {
        TransactionResponseDTO transaction = transactionService.findById(id);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/pagos/cargo-tarjeta")
    public ResponseEntity<CargoResponseApiDTO> ejecutarCobro(@RequestBody CargoRequestApiDTO request){
        CargoResponseApiDTO cargo = transactionService.ejecutarCobro(request);
        return ResponseEntity.ok(cargo);
    }

}
