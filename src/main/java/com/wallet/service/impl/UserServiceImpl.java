package com.wallet.service.impl;

import com.wallet.dto.request.UserRegisterDTO;
import com.wallet.dto.response.UserResponseDTO;
import com.wallet.entity.User;
import com.wallet.exception.ResourceNotFoundException;
import com.wallet.mapper.UserMapper;
import com.wallet.repository.UserRepository;
import com.wallet.service.UserService;
import com.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final WalletService walletService;

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponseDTO updateUser(String email, UserRegisterDTO userDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userMapper.update(userDTO, user);
        User userUpdated = userRepository.save(user);
        return userMapper.toResponseDTO(userUpdated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        walletService.deletedWalletByUserEmail(user.getEmail());
        userRepository.delete(user);
    }
}
