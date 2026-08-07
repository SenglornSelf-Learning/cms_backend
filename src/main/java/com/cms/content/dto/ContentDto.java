package com.cms.content.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ContentDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ContentResponse {
		private Integer id;
		private String uuid;
		private String slug;
		private String keyword;
		private String title;
		private String description;
		private String thumbnail;
		private String editor;
		private LocalDateTime createdAt;
		private Integer categoryId;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ContentRequest {
		private String uuid;
		private String slug;
		private String keyword;

		@NotBlank(message = "Title is required")
		private String title;

		private String description;
		private String thumbnail;
		private String editor;

		@NotNull(message = "Category id is required")
		private Integer categoryId;
	}
}
