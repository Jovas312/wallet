package com.wallet.service;

import com.wallet.dto.request.AuthDTO;
import com.wallet.dto.request.UserRegisterDTO;
import com.wallet.dto.response.AuthResponseDTO;

public interface AuthService {

    AuthResponseDTO register(UserRegisterDTO userDTO);

    AuthResponseDTO login(AuthDTO authDTO);
}
