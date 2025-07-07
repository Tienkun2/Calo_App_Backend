package com.dev.CaloApp.mapper;

import com.dev.CaloApp.dto.request.UserCreationRequest;
import com.dev.CaloApp.dto.request.UserUpdateRequest;
import com.dev.CaloApp.dto.response.UserResponse;
import com.dev.CaloApp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
@Component
// Chỉ cập nhật các trường khác null
// Chuyển đổi giữa Entity - DTO
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "mealLogs", ignore = true)
    User toUser(UserCreationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "mealLogs", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    UserResponse toUserResponse(User user);
}
