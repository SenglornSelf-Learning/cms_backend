package com.cms.userManagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cms.common.response.ResponseBody;
import com.cms.common.web.HttpRequestUtils;
import com.cms.userManagement.dto.RoleDto.RoleRequest;
import com.cms.userManagement.dto.RoleDto.RoleResponse;
import com.cms.userManagement.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles")
@RequiredArgsConstructor
public class RoleController {

	private final RoleService roleService;

	@GetMapping("/list")
	@Operation(summary = "List roles")
	public ResponseBody<List<RoleResponse>> list() {
		return ResponseBody.ok("Successfully retrieved roles.", roleService.findAll());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create role")
	public ResponseBody<RoleResponse> create(
			@Valid @RequestBody RoleRequest request,
			HttpServletRequest servletRequest) {
		RoleResponse role = roleService.create(
				request,
				HttpRequestUtils.clientIp(servletRequest),
				HttpRequestUtils.clientName(servletRequest));
		return ResponseBody.ok("Created Success", role);
	}

	@PutMapping("/update/{id}")
	@Operation(summary = "Update role")
	public ResponseBody<RoleResponse> update(
			@PathVariable("id") Integer id,
			@Valid @RequestBody RoleRequest request,
			HttpServletRequest servletRequest) {
		return ResponseBody.ok("Updated Success", roleService.update(
				id,
				request,
				HttpRequestUtils.clientIp(servletRequest),
				HttpRequestUtils.clientName(servletRequest)));
	}

	@DeleteMapping("/delete/{id}")
	@Operation(summary = "Delete role")
	public ResponseBody<Void> delete(@PathVariable("id") Integer id, HttpServletRequest servletRequest) {
		roleService.delete(
			id,
			HttpRequestUtils.clientIp(servletRequest),
			HttpRequestUtils.clientName(servletRequest));
		return ResponseBody.ok("Deleted Success", null);
	}
}
