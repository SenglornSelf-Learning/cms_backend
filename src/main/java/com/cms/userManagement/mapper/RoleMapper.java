package com.cms.userManagement.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.cms.userManagement.dto.RoleDto.RoleRequest;
import com.cms.userManagement.dto.RoleDto.RoleResponse;
import com.cms.userManagement.model.Role;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

	@Mapping(target = "roleType", source = "roleTypeName")
	RoleResponse toResponse(Role role);

	List<RoleResponse> toResponseList(List<Role> roles);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userRoles", ignore = true)
	@Mapping(target = "roleTypeName", source = "roleType")
	Role toEntity(RoleRequest request);
}
