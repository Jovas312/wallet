package com.wallet.service.impl;

import com.wallet.dto.response.WalletResponseDTO;
import com.wallet.entity.Wallet;
import com.wallet.exception.AuthenticationException;
import com.wallet.exception.ResourceNotFoundException;
import com.wallet.mapper.WalletMapper;
import com.wallet.repository.WalletRepository;
import com.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Override
    public WalletResponseDTO getWalletByUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Authentication required");
        }
        Wallet wallet = walletRepository.findByUser_Email(authentication.getName()).orElseThrow(() -> new RuntimeException("Wallet not found"));
        return walletMapper.toResponseDTO(wallet);
    }

    @Override
    public void createWallet(Wallet wallet) {
        walletRepository.save(wallet);
    }

    @Override
    public void deletedWalletByUserEmail(String email) {
        Wallet wallet =  walletRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        if (wallet.getBalance().compareTo(BigDecimal.ZERO) != 0){
            throw new IllegalArgumentException("Cannot delete wallet: Balance is not zero");
        }
        walletRepository.delete(wallet);
    }
}
