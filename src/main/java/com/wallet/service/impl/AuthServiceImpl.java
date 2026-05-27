package com.wallet.service.impl;

import com.wallet.dto.request.AuthDTO;
import com.wallet.dto.request.UserRegisterDTO;
import com.wallet.dto.response.UserResponseDTO;
import com.wallet.dto.response.WalletResponseDTO;
import com.wallet.entity.User;
import com.wallet.entity.Wallet;
import com.wallet.exception.ResourceNotFoundException;
import com.wallet.mapper.UserMapper;
import com.wallet.repository.UserRepository;
import com.wallet.service.AuthService;
import com.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final WalletService walletService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponseDTO register(UserRegisterDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.email()) || userRepository.existsByDocumentId(userDTO.documentId())){
            throw new IllegalArgumentException("Email or DocumentId already exist");
        }
        User user = userMapper.toEntity(userDTO);
        User userCreated = userRepository.save(user);

        Wallet wallet = Wallet.builder()
                .user(userCreated)
                .balance(BigDecimal.ZERO)
                .build();

        walletService.createWallet(wallet);

        return userMapper.toResponseDTO(userCreated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletResponseDTO login(AuthDTO authDTO) {
        User user = userRepository.findByEmail(authDTO.email())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Password or Email"));
        if (!user.getPassword().equals(authDTO.password())){
            throw new IllegalArgumentException("Invalid Password or Email");
        }

        return walletService.getWalletByUserEmail(user.getEmail());
    }
}
