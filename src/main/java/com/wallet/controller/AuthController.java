package com.wallet.controller;

import com.wallet.dto.request.AuthDTO;
import com.wallet.dto.request.UserRegisterDTO;
import com.wallet.dto.response.UserResponseDTO;
import com.wallet.dto.response.WalletResponseDTO;
import com.wallet.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegisterDTO userDTO){
        UserResponseDTO userResponseDTO = authService.register(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<WalletResponseDTO> login(@RequestBody AuthDTO authDTO){
        WalletResponseDTO walletResponseDTO = authService.login(authDTO);
        return ResponseEntity.ok(walletResponseDTO);
    }
}
