package com.cms.content.controller;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cms.common.response.PageResponse;
import com.cms.common.response.ResponseBody;
import com.cms.common.web.HttpRequestUtils;
import com.cms.content.dto.ContentDto.ContentRequest;
import com.cms.content.dto.ContentDto.ContentResponse;
import com.cms.content.dto.ContentDto.ThumbnailDownload;
import com.cms.content.service.ContentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contents")
@Tag(name = "Contents")
@RequiredArgsConstructor
public class ContentController {

	private final ContentService contentService;

	@GetMapping("/list")
	@Operation(summary = "List contents")
	public ResponseBody<PageResponse<ContentResponse>> list(
		@RequestParam(name = "pageIndex", defaultValue = "1") int pageIndex,
		@RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
		@RequestParam(name = "orderBy", defaultValue = "createdAt,DESC") String orderBy,
		@RequestParam(name = "title", required = false) String title,
		@RequestParam(name = "editor", required = false) String editor
	) {
		PageResponse<ContentResponse> contents = contentService.findAll(pageIndex, pageSize, orderBy, title, editor);
		return ResponseBody.ok("Successfully retrieved contents.", contents);
	}

	@GetMapping("/getById/{id}")
	@Operation(summary = "Get content by id")
	public ResponseBody<ContentResponse> getById(@PathVariable("id") Integer id) {
		return ResponseBody.ok("Success", contentService.getById(id));
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create content")
	public ResponseBody<ContentResponse> create(
			@Valid @RequestBody ContentRequest request,
			HttpServletRequest servletRequest) {
		return ResponseBody.ok("Created Success", contentService.create(
				request,
				List.of(),
				HttpRequestUtils.clientIp(servletRequest),
				HttpRequestUtils.clientName(servletRequest)));
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create content with thumbnails")
	public ResponseBody<ContentResponse> createWithFiles(
			@Valid @ModelAttribute ContentRequest request,
			@RequestParam(name = "thumbnails", required = false) MultipartFile[] thumbnails,
			HttpServletRequest servletRequest) {
		return ResponseBody.ok("Created Success", contentService.create(
				request,
				asFileList(thumbnails),
				HttpRequestUtils.clientIp(servletRequest),
				HttpRequestUtils.clientName(servletRequest)));
	}

	@PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Update content")
	public ResponseBody<ContentResponse> update(
			@PathVariable("id") Integer id,
			@Valid @RequestBody ContentRequest request,
			HttpServletRequest servletRequest) {
		return ResponseBody.ok("Updated Success", contentService.update(
				id,
				request,
				List.of(),
				List.of(),
				HttpRequestUtils.clientIp(servletRequest),
				HttpRequestUtils.clientName(servletRequest)));
	}

	@PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Update content with thumbnails")
	public ResponseBody<ContentResponse> updateWithFiles(
			@PathVariable("id") Integer id,
			@Valid @ModelAttribute ContentRequest request,
			@RequestParam(name = "thumbnails", required = false) MultipartFile[] thumbnails,
			@RequestParam(name = "deletedThumbnailIds", required = false) List<Integer> deletedThumbnailIds,
			HttpServletRequest servletRequest) {
		return ResponseBody.ok("Updated Success", contentService.update(
				id,
				request,
				asFileList(thumbnails),
				deletedThumbnailIds == null ? List.of() : deletedThumbnailIds,
				HttpRequestUtils.clientIp(servletRequest),
				HttpRequestUtils.clientName(servletRequest)));
	}

	@DeleteMapping("/delete/{id}")
	@Operation(summary = "Soft-delete content")
	public ResponseBody<Void> delete(@PathVariable("id") Integer id, HttpServletRequest servletRequest) {
		contentService.delete(
				id,
				HttpRequestUtils.clientIp(servletRequest),
				HttpRequestUtils.clientName(servletRequest));
		return ResponseBody.ok("Deleted Success", null);
	}

	@GetMapping("/{id}/thumbnails/{fileId}")
	@Operation(summary = "Download content thumbnail")
	public ResponseEntity<Resource> downloadThumbnail(
			@PathVariable("id") Integer id,
			@PathVariable("fileId") Integer fileId) {
		ThumbnailDownload download = contentService.getThumbnailDownload(id, fileId);
		MediaType contentType = download.getContentType() == null || download.getContentType().isBlank()
				? MediaType.APPLICATION_OCTET_STREAM
				: MediaType.parseMediaType(download.getContentType());
		return ResponseEntity.ok()
				.contentType(contentType)
				.contentLength(download.getFileSize())
				.header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
						.filename(download.getOriginalFileName(), StandardCharsets.UTF_8)
						.build()
						.toString())
				.body(download.getResource());
	}

	private static List<MultipartFile> asFileList(MultipartFile[] files) {
		if (files == null || files.length == 0) {
			return List.of();
		}
		return Arrays.asList(files);
	}
}
