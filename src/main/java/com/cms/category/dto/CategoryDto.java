package com.cms.category.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CategoryDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CategoryResponse {
		private Integer id;
		private String name;
		private String deletedYn;
		private LocalDateTime createdAt;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CategoryRequest {
		@NotBlank(message = "Name is required")
		private String name;
	}
}
