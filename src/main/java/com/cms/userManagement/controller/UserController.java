package com.cms.userManagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cms.common.response.PageResponse;
import com.cms.common.response.ResponseBody;
import com.cms.common.web.HttpRequestUtils;
import com.cms.userManagement.dto.UserDto.UserRequest;
import com.cms.userManagement.dto.UserDto.UserResponse;
import com.cms.userManagement.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/list")
	@Operation(summary = "List users")
	public ResponseBody<PageResponse<UserResponse>> list(
			@RequestParam(name = "pageIndex", defaultValue = "1") int pageIndex,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
			@RequestParam(name = "orderBy", defaultValue = "createdAt,DESC") String orderBy,
			@RequestParam(name = "username", required = false) String username,
			@RequestParam(name = "email", required = false) String email,
			@RequestParam(name = "phone", required = false) String phone,
			@RequestParam(name = "role", required = false) String role) {
		PageResponse<UserResponse> users = userService.findAll(
				pageIndex,
				pageSize,
				orderBy,
				username,
				email,
				phone,
				role);
		return ResponseBody.ok("Successfully retrieved users.", users);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create user")
	public ResponseBody<UserResponse> create(
			@Valid @RequestBody UserRequest request,
			HttpServletRequest servletRequest) {

		UserResponse user = userService.create(
				request,
				HttpRequestUtils.clientIp(servletRequest),
				HttpRequestUtils.clientName(servletRequest));
				
		return ResponseBody.ok("Created Success", user);
	}

	@PutMapping("/update/{id}")
	@Operation(summary = "Update user")
	public ResponseBody<UserResponse> update(
			@PathVariable("id") Integer id,
			@Valid @RequestBody UserRequest request,
			HttpServletRequest servletRequest) {
		return ResponseBody.ok("Updated Success", userService.update(id, request, HttpRequestUtils.clientIp(servletRequest), HttpRequestUtils.clientName(servletRequest)));
	}
}
