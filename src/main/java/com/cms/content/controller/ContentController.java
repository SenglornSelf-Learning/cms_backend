package com.cms.content.controller;

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
import com.cms.content.dto.ContentDto.ContentRequest;
import com.cms.content.dto.ContentDto.ContentResponse;
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
	public ResponseBody<List<ContentResponse>> list() {
		return ResponseBody.ok("Success", contentService.findAll());
	}

	@GetMapping("/getById/{id}")
	@Operation(summary = "Get content by id")
	public ResponseBody<ContentResponse> getById(@PathVariable("id") Integer id) {
		return ResponseBody.ok("Success", contentService.getById(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create content")
	public ResponseBody<ContentResponse> create(
			@Valid @RequestBody ContentRequest request,
			HttpServletRequest servletRequest) {
		return ResponseBody.ok("Created Success", contentService.create(
				request,
				HttpRequestUtils.clientIp(servletRequest),
				HttpRequestUtils.clientName(servletRequest)));
	}

	@PutMapping("/update/{id}")
	@Operation(summary = "Update content")
	public ResponseBody<ContentResponse> update(
			@PathVariable("id") Integer id,
			@Valid @RequestBody ContentRequest request,
			HttpServletRequest servletRequest) {
		return ResponseBody.ok("Updated Success", contentService.update(
				id,
				request,
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
}
