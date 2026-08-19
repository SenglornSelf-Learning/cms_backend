package com.cms.userManagement.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.cms.userManagement.dto.UserDto.UserRequest;
import com.cms.userManagement.dto.UserDto.UserResponse;
import com.cms.userManagement.model.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = RoleMapper.class)
public interface UserMapper {

	UserResponse toResponse(User user);

	List<UserResponse> toResponseList(List<User> users);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "roles", ignore = true)
	User toEntity(UserRequest request);
}
