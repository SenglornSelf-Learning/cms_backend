package com.cms.userManagement.dto;

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
		private String roleType;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RoleResponse {
		private Integer id;
		private String roleType;
	}
}
