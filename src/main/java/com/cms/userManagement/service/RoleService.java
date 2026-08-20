package com.cms.userManagement.service;

import java.util.List;

import com.cms.userManagement.dto.RoleDto.RoleRequest;
import com.cms.userManagement.dto.RoleDto.RoleResponse;

public interface RoleService {

	List<RoleResponse> findAll();

	RoleResponse create(RoleRequest request, String clientIp, String clientName);

	RoleResponse update(Integer id, RoleRequest request, String clientIp, String clientName);

	void delete(Integer id, String clientIp, String clientName);
}
