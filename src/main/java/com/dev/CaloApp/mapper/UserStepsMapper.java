package com.dev.CaloApp.mapper;

import com.dev.CaloApp.dto.request.UserStepsRequest;
import com.dev.CaloApp.dto.response.UserStepsResponse;
import com.dev.CaloApp.entity.UserSteps;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStepsMapper {
    public UserStepsResponse toUserStepsResponse(UserSteps userSteps);
    public UserSteps toUserSteps(UserStepsRequest userStepsRequest);
}
