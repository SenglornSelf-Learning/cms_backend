package com.cms.userManagement.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.cms.userManagement.dto.RoleDto.RoleRequest;
import com.cms.userManagement.dto.RoleDto.RoleResponse;
import com.cms.userManagement.model.Role;
import com.cms.userManagement.model.RoleName;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

	RoleResponse toResponse(Role role);

	List<RoleResponse> toResponseList(List<Role> roles);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "users", ignore = true)
	@Mapping(target = "name", source = "name")
	Role toEntity(RoleRequest request);

	default RoleName toRoleName(String name) {
		return RoleName.from(name);
	}
}
