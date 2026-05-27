package com.wallet.mapper;

import com.wallet.dto.request.TransferRequestDTO;
import com.wallet.dto.response.TransactionResponseDTO;
import com.wallet.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sourceWallet", ignore = true)
    @Mapping(target = "destinationWallet", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Transaction toEntity(TransferRequestDTO transferRequestDTO);

    @Mapping(source = "sourceWallet.user.email", target = "sourceUserEmail")
    @Mapping(source = "destinationWallet.user.email", target = "destinationUserEmail")
    TransactionResponseDTO toResponseDTO(Transaction transaction);

}
