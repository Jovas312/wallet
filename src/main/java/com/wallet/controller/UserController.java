package com.wallet.controller;

import com.wallet.dto.request.UserRegisterDTO;
import com.wallet.dto.response.UserResponseDTO;
import com.wallet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        UserResponseDTO userResponseDTO = userService.getUserByEmail(email);
        return ResponseEntity.ok(userResponseDTO);
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<UserResponseDTO> updateuser(@PathVariable String email, @RequestBody UserRegisterDTO userDTO){
        UserResponseDTO userResponseDTO = userService.updateUser(email, userDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/deleted/{email}")
    public void deleteUser(@PathVariable String email){
        userService.deleteUserByEmail(email);
    }

}
