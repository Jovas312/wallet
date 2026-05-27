package com.wallet.service;

import com.wallet.dto.request.UserRegisterDTO;
import com.wallet.dto.response.UserResponseDTO;

import java.util.Optional;

public interface UserService {

    UserResponseDTO getUserByEmail(String email);

    UserResponseDTO updateUser(String email, UserRegisterDTO userDTO);

    void deleteUserByEmail(String email);

}
