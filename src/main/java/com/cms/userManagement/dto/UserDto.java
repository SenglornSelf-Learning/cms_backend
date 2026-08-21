package com.cms.userManagement.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.cms.userManagement.dto.RoleDto.RoleRequest;
import com.cms.userManagement.dto.RoleDto.RoleResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class UserDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserRequest {
		@Size(max = 50, message = "Username must be at most 50 characters")
		private String username;

		@Size(max = 100, message = "Password must be at most 100 characters")
		private String password;

		@Size(max = 100, message = "Email must be at most 100 characters")
		private String email;

		@Size(max = 15, message = "Phone must be at most 15 characters")
		@Pattern(regexp = "^\\d+$", message = "Phone must be a number")
		private String phone;

		@Valid
		private List<RoleRequest> roles;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserResponse {
		private Integer id;
		private String username;
		private String email;
		private String phone;
		private List<RoleResponse> roles;
		private String deletedYn;
		private LocalDateTime createdAt;
	}
}
