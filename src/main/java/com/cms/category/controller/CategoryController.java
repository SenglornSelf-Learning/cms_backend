package com.cms.category.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cms.category.dto.CategoryDto.CategoryRequest;
import com.cms.category.dto.CategoryDto.CategoryResponse;
import com.cms.category.service.CategoryService;
import com.cms.common.response.PageResponse;
import com.cms.common.response.ResponseBody;
import com.cms.common.web.HttpRequestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping("/list")
	@Operation(summary = "List categories")
	public ResponseBody<PageResponse<CategoryResponse>> list(
			@RequestParam(name = "pageIndex", defaultValue = "1") int pageIndex,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
			@RequestParam(name = "orderBy", defaultValue = "createdAt,DESC") String orderBy) {

		PageResponse<CategoryResponse> categories = categoryService.findAll(
				pageIndex,
				pageSize,
				orderBy);
		return ResponseBody.ok("Retrieved successfully.", categories);
	}

	@GetMapping("/getById/{id}")
	@Operation(summary = "Get category by id")
	public ResponseBody<CategoryResponse> getById(@PathVariable("id") Integer id) {
		return ResponseBody.ok("Success", categoryService.getById(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create category")
	public ResponseBody<CategoryResponse> create(
			@Valid @RequestBody CategoryRequest request,
			HttpServletRequest servletRequest) {
		return ResponseBody.ok("Created Success", categoryService.create(
				request,
				HttpRequestUtils.clientIp(servletRequest),
				HttpRequestUtils.clientName(servletRequest)));
	}

	@PutMapping("/update/{id}")
	@Operation(summary = "Update category")
	public ResponseBody<CategoryResponse> update(
			@PathVariable("id") Integer id,
			@Valid @RequestBody CategoryRequest request,
			HttpServletRequest servletRequest) {
		return ResponseBody.ok("Updated Success", categoryService.update(
				id,
				request,
				HttpRequestUtils.clientIp(servletRequest),
				HttpRequestUtils.clientName(servletRequest)));
	}

	@DeleteMapping("/delete/{id}")
	@Operation(summary = "Soft-delete category")
	public ResponseBody<Void> delete(@PathVariable("id") Integer id, HttpServletRequest servletRequest) {
		categoryService.delete(
				id,
				HttpRequestUtils.clientIp(servletRequest),
				HttpRequestUtils.clientName(servletRequest));
		return ResponseBody.ok("Deleted Success", null);
	}
}
