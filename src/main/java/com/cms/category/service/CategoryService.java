package com.cms.category.service;

import com.cms.category.dto.CategoryDto.CategoryRequest;
import com.cms.category.dto.CategoryDto.CategoryResponse;
import com.cms.common.response.PageResponse;

public interface CategoryService {

	PageResponse<CategoryResponse> findAll(Integer pageIndex, Integer pageSize, String orderBy, String name);

	CategoryResponse getById(Integer id);

	CategoryResponse create(CategoryRequest request, String clientIp, String clientName);

	CategoryResponse update(Integer id, CategoryRequest request, String clientIp, String clientName);

	void delete(Integer id, String clientIp, String clientName);
}
