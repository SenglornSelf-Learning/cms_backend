package com.cms.userManagement.dto;

import com.cms.userManagement.model.RoleName;

import jakarta.validation.constraints.NotBlank;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
public class RoleDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RoleRequest {
		@NotBlank(message = "Role name is required")
		private String name;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RoleResponse {
		private Integer id;
		private RoleName name;
	}
}
