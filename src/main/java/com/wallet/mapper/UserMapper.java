package com.wallet.mapper;

import com.wallet.dto.request.UserRegisterDTO;
import com.wallet.dto.response.AuthResponseDTO;
import com.wallet.dto.response.UserResponseDTO;
import com.wallet.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "role",  ignore = true)
    User toEntity(UserRegisterDTO userRegisterDTO);

    UserResponseDTO toResponseDTO(User user);

    @Mapping(target = "token", source = "token")
    AuthResponseDTO toAuthResponseDTO(User user, String token);

    List<UserResponseDTO> toResponseDTO(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    void update(UserRegisterDTO userRegisterDTO, @MappingTarget User user);
}
