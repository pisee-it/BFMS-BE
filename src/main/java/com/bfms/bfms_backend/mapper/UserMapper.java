package com.bfms.bfms_backend.mapper;

import com.bfms.bfms_backend.dtos.req.UserRequest;
import com.bfms.bfms_backend.dtos.res.UserResponse;
import com.bfms.bfms_backend.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(AppUser user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Sẽ được set thủ công sau khi mã hóa
    @Mapping(target = "authorities", ignore = true)
    AppUser toEntity(UserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Sẽ được set thủ công sau khi mã hóa
    @Mapping(target = "authorities", ignore = true)
    void updateEntity(UserRequest request, @MappingTarget AppUser user);
}
