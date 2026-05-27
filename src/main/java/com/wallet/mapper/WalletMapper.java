package com.wallet.mapper;

import com.wallet.dto.response.WalletResponseDTO;
import com.wallet.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "userEmail")
    WalletResponseDTO toResponseDTO(Wallet wallet);
}
