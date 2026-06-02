package com.wallet.service.impl;

import com.wallet.dto.request.AuthDTO;
import com.wallet.dto.request.UserRegisterDTO;
import com.wallet.dto.response.AuthResponseDTO;
import com.wallet.entity.User;
import com.wallet.entity.Wallet;
import com.wallet.entity.enums.Role;
import com.wallet.exception.ResourceNotFoundException;
import com.wallet.mapper.UserMapper;
import com.wallet.repository.UserRepository;
import com.wallet.service.AuthService;
import com.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthResponseDTO register(UserRegisterDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.email()) || userRepository.existsByDocumentId(userDTO.documentId())){
            throw new IllegalArgumentException("Email or DocumentId already exist");
        }
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        user.setRole(Role.USER);
        User userCreated = userRepository.save(user);

        String jwtToken = jwtService.generateToken(userCreated);

        Wallet wallet = Wallet.builder()
                .user(userCreated)
                .balance(BigDecimal.ZERO)
                .build();

        walletService.createWallet(wallet);

        return userMapper.toAuthResponseDTO(userCreated, jwtToken);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthResponseDTO login(AuthDTO authDTO) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authDTO.email(),
                            authDTO.password()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Password or Email");
        }

        User user = userRepository.findByEmail(authDTO.email())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Password or Email"));

        String jwtToken = jwtService.generateToken(user);

        return userMapper.toAuthResponseDTO(user, jwtToken);
    }
}
