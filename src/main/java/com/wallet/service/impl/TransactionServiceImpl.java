package com.wallet.service.impl;

import com.wallet.dto.request.*;
import com.wallet.dto.response.CargoResponseApiDTO;
import com.wallet.dto.response.TransactionResponseDTO;
import com.wallet.dto.response.WalletResponseDTO;
import com.wallet.entity.Transaction;
import com.wallet.entity.User;
import com.wallet.entity.Wallet;
import com.wallet.entity.enums.Status;
import com.wallet.entity.enums.Type;
import com.wallet.exception.*;
import com.wallet.mapper.TransactionMapper;
import com.wallet.mapper.WalletMapper;
import com.wallet.repository.TransactionRepository;
import com.wallet.repository.WalletRepository;
import com.wallet.service.TransactionService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final WalletMapper walletMapper;
    private final WalletRepository walletRepository;
    private final ExternalApiGateway externalApiGateway;
    private final SimpMessagingTemplate messagingTemplate;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletResponseDTO deposit(DepositRequestDTO depositDTO) {
        UUID walletId = null;
        try {
            if (depositDTO.amount().compareTo(BigDecimal.ZERO) <= 0){
                throw new IllegalArgumentException("Amount must be greater than zero");
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new AuthenticationException("Authentication required");
            }
            Wallet wallet = walletRepository.findByUser_Email(authentication.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

            walletId = wallet.getId();

            BigDecimal depositAmount = depositDTO.amount();
            wallet.setBalance(wallet.getBalance().add(depositAmount));
            Wallet savedWallet = walletRepository.save(wallet);
            String mensajeExito = "¡Depósito exitoso! Se han abonado $" + depositAmount + " a tu cuenta.";
            messagingTemplate.convertAndSend("/topic/wallet/" + walletId, mensajeExito);
            return walletMapper.toResponseDTO(savedWallet);
        } catch (Exception e) {
            String messageFaild = "El deposito de $" + depositDTO.amount() + "ha fallado. Motivo: " + e.getMessage();
            if (walletId != null){
                messagingTemplate.convertAndSend("/topic/wallet" + walletId, messageFaild);
            }
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionResponseDTO transfer(TransferRequestDTO transferDTO) {
        if (transferDTO.amount().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Authentication required");
        }
        Wallet sourceWallet = walletRepository.findByUser_Email(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet source not found"));

        Wallet destinationWallet = walletRepository.findByUser_Email(transferDTO.destinationEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet destination not found"));

        if (sourceWallet.getBalance().compareTo(transferDTO.amount()) < 0){
            throw new IllegalArgumentException("Insufficient funds to complete this transfer");
        }
        try {
            sourceWallet.setBalance(sourceWallet.getBalance().subtract(transferDTO.amount()));
            walletRepository.save(sourceWallet);

            destinationWallet.setBalance(destinationWallet.getBalance().add(transferDTO.amount()));
            walletRepository.save(destinationWallet);

            Transaction transaction = transactionMapper.toEntity(transferDTO);

            transaction.setSourceWallet(sourceWallet);
            transaction.setDestinationWallet(destinationWallet);
            transaction.setType(Type.TRANSFER);
            transaction.setStatus(Status.SUCCESS);

            transactionRepository.save(transaction);

            return transactionMapper.toResponseDTO(transaction);
        } catch (OptimisticLockException e) {
            throw new TransactionNotCompleted("Transaction not completed");
        }
    }

    @Override
    public Page<TransactionResponseDTO> transactions(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Authentication required");
        }
        Page<Transaction> transactions = transactionRepository
                .findAllBySourceWallet_User_Email(authentication.getName(), pageable);
        return transactions.map(transactionMapper::toResponseDTO);
    }

    @Override
    public TransactionResponseDTO findById(UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Authentication required");
        }
        String currentUserEmail = authentication.getName();
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        String ownerEmail = transaction.getSourceWallet().getUser().getEmail();
        if (!ownerEmail.equals(currentUserEmail)){
            throw new AccessDeniedException("You do not have permission to view this transaction");
        }
        return transactionMapper.toResponseDTO(transaction);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CargoResponseApiDTO ejecutarCobro(CargoRequestApiDTO cargoRequestApiDTO) {

        System.out.println("Inicando procesamiento de cobro por un monto de: " + cargoRequestApiDTO.amount());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            User userLogued = (User) authentication.getPrincipal();
            String firstName = userLogued.getFirstName();
            String lastName = userLogued.getLastName();
            String email = userLogued.getEmail();

            CustomerRequestDTO customerAuto = new CustomerRequestDTO(firstName, lastName, email);

            CargoRequestApiDTO requestCompleted = new CargoRequestApiDTO(
                    cargoRequestApiDTO.method(),
                    cargoRequestApiDTO.amount(),
                    cargoRequestApiDTO.description(),
                    cargoRequestApiDTO.sourceId(),
                    cargoRequestApiDTO.deviceSessionId(),
                    customerAuto
            );

            CargoResponseApiDTO cobroResponse = externalApiGateway.procesarCargoTarjeta(requestCompleted);

            if (cobroResponse.status() != null && cobroResponse.status().equalsIgnoreCase("completed")){

                System.out.println("Cobro completado por un monto de: " +  cobroResponse.amount());

                Wallet wallet = walletRepository.findByUser_Email(authentication.getName())
                        .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

                BigDecimal depositAmount = cargoRequestApiDTO.amount();
                wallet.setBalance(wallet.getBalance().add(depositAmount));
                walletRepository.saveAndFlush(wallet);

            } else {
                throw new PaymentException("El pago no pudo ser procesado por la pasarela: " + cobroResponse.status());
            }

            return cobroResponse;

        }
        throw new InsufficientAuthenticationException("Usuario no autenticado o sesión inválida para procesar el pago.");
    }

}
