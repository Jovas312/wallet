package com.wallet.service.impl;

import com.wallet.dto.request.UserRegisterDTO;
import com.wallet.dto.response.UserResponseDTO;
import com.wallet.entity.User;
import com.wallet.exception.AuthenticationException;
import com.wallet.exception.ResourceNotFoundException;
import com.wallet.mapper.UserMapper;
import com.wallet.repository.UserRepository;
import com.wallet.service.UserService;
import com.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final WalletService walletService;
    private static final String NOT_FOUND = "User not found";

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND));
        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponseDTO updateUser(UserRegisterDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Authentication required");
        }
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND));

        userMapper.update(userDTO, user);
        User userUpdated = userRepository.save(user);
        return userMapper.toResponseDTO(userUpdated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserByEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Authentication required");
        }
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND));
        walletService.deletedWalletByUserEmail(user.getEmail());
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND));
    }
}
