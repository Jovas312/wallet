package com.wallet.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.service.impl.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debe retornar 401 UNAUTHORIZED cuando se solicita una ruta protegida sin token")
    void solicitarRutaProtegidaSinToken_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Debe retornar 200 OK al solicitar transacciones con un token valido")
    void solicitarRutaProtegidaConToken_DebeRetornar200Ok() throws Exception {
        String userEmail = "jov321@gmail.com";
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        String validToken = jwtService.generateToken(userDetails);

        mockMvc.perform(get("/api/v1/transactions/" + userEmail)
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Debe retornar 404 NOT FOUND si el usuario intenta buscar un ID de transaccion que no le pertenece")
    void buscarTransaccionAjena_DebeRetornar404() throws Exception {
        String userEmail = "mac321@gmail.com";
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        String tokenUsuario1 = jwtService.generateToken(userDetails);

        UUID idTransaccionAjena = UUID.fromString("95bbb5aa-3382-4500-9353-7757da9cfa84");

        mockMvc.perform(get("/api/v1/transactions/" + idTransaccionAjena + "/" + userEmail)
                .header("Authorization", "Bearer " + tokenUsuario1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}
