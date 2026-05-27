package com.wallet.service;

import com.wallet.dto.response.WalletResponseDTO;
import com.wallet.entity.Wallet;

public interface WalletService {

    WalletResponseDTO getWalletByUserEmail(String email);

    void createWallet(Wallet wallet);

    void deletedWalletByUserEmail(String email);

}
