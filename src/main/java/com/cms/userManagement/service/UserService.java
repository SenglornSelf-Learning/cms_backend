package com.cms.userManagement.service;

import com.cms.common.response.PageResponse;
import com.cms.userManagement.dto.UserDto.UserRequest;
import com.cms.userManagement.dto.UserDto.UserResponse;

public interface UserService {

	PageResponse<UserResponse> findAll(
			Integer pageIndex,
			Integer pageSize,
			String orderBy,
			String username,
			String email,
			String phone,
			String role);

	UserResponse create(UserRequest request, String clientIp, String clientName);
}
