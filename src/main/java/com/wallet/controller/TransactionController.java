package com.wallet.controller;

import com.wallet.dto.request.DepositRequestDTO;
import com.wallet.dto.request.TransferRequestDTO;
import com.wallet.dto.response.TransactionResponseDTO;
import com.wallet.dto.response.WalletResponseDTO;
import com.wallet.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

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

    @GetMapping
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactions(@PageableDefault(page = 0, size = 20)Pageable pageable) {
        Page<TransactionResponseDTO> transactions = transactionService.transactions(pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable UUID id) {
        TransactionResponseDTO transaction = transactionService.findById(id);
        return ResponseEntity.ok(transaction);
    }


}
