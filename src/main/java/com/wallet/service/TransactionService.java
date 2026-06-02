package com.wallet.service;

import com.wallet.dto.request.DepositRequestDTO;
import com.wallet.dto.request.TransferRequestDTO;
import com.wallet.dto.response.TransactionResponseDTO;
import com.wallet.dto.response.WalletResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TransactionService {

    WalletResponseDTO deposit(DepositRequestDTO depositDTO);

    TransactionResponseDTO transfer(TransferRequestDTO transferDTO);

    Page<TransactionResponseDTO> transactions(Pageable pageable);

    TransactionResponseDTO findById(UUID id);
}
