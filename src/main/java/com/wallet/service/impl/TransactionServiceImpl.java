package com.wallet.service.impl;

import com.wallet.dto.request.DepositRequestDTO;
import com.wallet.dto.request.TransferRequestDTO;
import com.wallet.dto.response.TransactionResponseDTO;
import com.wallet.dto.response.WalletResponseDTO;
import com.wallet.entity.Transaction;
import com.wallet.entity.Wallet;
import com.wallet.entity.enums.Status;
import com.wallet.entity.enums.Type;
import com.wallet.exception.ResourceNotFoundException;
import com.wallet.mapper.TransactionMapper;
import com.wallet.mapper.WalletMapper;
import com.wallet.repository.TransactionRepository;
import com.wallet.repository.WalletRepository;
import com.wallet.service.TransactionService;
import com.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final WalletService walletService;
    private final WalletMapper walletMapper;
    private final WalletRepository walletRepository;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletResponseDTO deposit(DepositRequestDTO depositDTO, String email) {
        if (depositDTO.amount().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        BigDecimal depositAmount = depositDTO.amount();
        wallet.setBalance(wallet.getBalance().add(depositAmount));
        Wallet savedWallet = walletRepository.save(wallet);
        return walletMapper.toResponseDTO(savedWallet);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionResponseDTO transfer(TransferRequestDTO transferDTO, String email) {
        if (transferDTO.amount().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Wallet sourceWallet = walletRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        Wallet destinationWallet = walletRepository.findByUser_Email(transferDTO.destinationEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (sourceWallet.getBalance().compareTo(transferDTO.amount()) < 0){
            throw new IllegalArgumentException("Insufficient funds to complete this transfer");
        }

        sourceWallet.setBalance(sourceWallet.getBalance().subtract(transferDTO.amount()));
        walletRepository.save(sourceWallet);

        destinationWallet.setBalance(destinationWallet.getBalance().add(transferDTO.amount()));
        walletRepository.save(destinationWallet);

        Transaction transaction = transactionMapper.toEntity(transferDTO);

        transaction.setSourceWallet(sourceWallet);
        transaction.setDestinationWallet(destinationWallet);
        transaction.setType(Type.TRANSFER);
        transaction.setStatus(Status.SUCCESS);

        transactionRepository.save(transaction);

        return transactionMapper.toResponseDTO(transaction);
    }

    @Override
    public Page<TransactionResponseDTO> transactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        return transactions.map(transactionMapper::toResponseDTO);
    }

    @Override
    public TransactionResponseDTO findById(UUID id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return transactionMapper.toResponseDTO(transaction);
    }
}
