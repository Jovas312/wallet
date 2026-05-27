package com.wallet.service;

import com.wallet.dto.request.AuthDTO;
import com.wallet.dto.request.UserRegisterDTO;
import com.wallet.dto.response.UserResponseDTO;
import com.wallet.dto.response.WalletResponseDTO;

public interface AuthService {

    UserResponseDTO register(UserRegisterDTO userDTO);

    WalletResponseDTO login(AuthDTO authDTO);
}
