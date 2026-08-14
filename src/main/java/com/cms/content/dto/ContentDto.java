package com.cms.content.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
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
	public static class ThumbnailResponse {
		private Integer id;
		private String originalFileName;
		private String contentType;
		private long fileSize;
		private String url;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ThumbnailDownload {
		private String originalFileName;
		private String contentType;
		private long fileSize;
		private Resource resource;
	}

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
		private List<ThumbnailResponse> thumbnails;
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
		private String editor;

		@NotNull(message = "Category id is required")
		private Integer categoryId;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class ContentFilesRequest extends ContentRequest {

		@ArraySchema(schema = @Schema(type = "string", format = "binary"))
		private List<MultipartFile> thumbnails;

		@Schema(description = "Existing thumbnail ids to remove on update.")
		private List<Integer> deletedThumbnailIds;
	}
}
