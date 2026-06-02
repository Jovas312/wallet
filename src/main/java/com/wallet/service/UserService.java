package com.wallet.service;

import com.wallet.dto.request.UserRegisterDTO;
import com.wallet.dto.response.UserResponseDTO;

public interface UserService {

    UserResponseDTO getUserByEmail(String email);

    UserResponseDTO updateUser(UserRegisterDTO userDTO);

    void deleteUserByEmail();

}
