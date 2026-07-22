package com.cms.category.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.cms.category.mapper.CategoryMapper;
import com.cms.category.model.Category;
import com.cms.category.dto.CategoryDto.CategoryRequest;
import com.cms.category.dto.CategoryDto.CategoryResponse;
import com.cms.category.repository.CategoryRepository;
import com.cms.category.service.CategoryService;
import com.cms.common.response.PageResponse;
import com.cms.common.web.HttpRequestUtils;
import com.cms.globleException.exception.CategoryException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

	private static final String ACTIVE = "N";
	private static final String DELETED = "Y";

	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;

	@Override
	public PageResponse<CategoryResponse> findAll(Integer pageIndex, Integer pageSize, String orderBy) {
		int setPageIndex = pageIndex == null || pageIndex < 1 ? 1 : pageIndex;
		int setPageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
		Sort sort = buildSort(orderBy);

		PageRequest pageable = PageRequest.of(setPageIndex - 1, setPageSize, sort);
		Page<Category> page = categoryRepository.findByDeletedYn(ACTIVE, pageable);

		return new PageResponse<>(
				categoryMapper.toResponseList(page.getContent()),
				page.getTotalElements(),
				setPageIndex,
				setPageSize,
				page.getTotalPages());
	}

	@Override
	public CategoryResponse getById(Integer id) {
		return categoryMapper.toResponse(findActiveEntity(id));
	}

	@Override
	@Transactional
	public CategoryResponse create(CategoryRequest request, String clientIp, String clientName) {
		if (!StringUtils.hasText(request.getName())) {
			throw CategoryException.badRequest("Category name is required");
		}
		Category entity = categoryMapper.toEntity(request);
		entity.setDeletedYn(ACTIVE);
		HttpRequestUtils.applyCreateAudit(entity, clientIp, clientName);
		Category saved = categoryRepository.save(entity);
		return categoryMapper.toResponse(saved);
	}

	@Override
	@Transactional
	public CategoryResponse update(Integer id, CategoryRequest request, String clientIp, String clientName) {
		Category category = findActiveEntity(id);
		categoryMapper.updateEntity(request, category);
		HttpRequestUtils.applyUpdateAudit(category, clientIp, clientName);
		Category saved = categoryRepository.save(category);
		return categoryMapper.toResponse(saved);
	}

	@Override
	@Transactional
	public void delete(Integer id, String clientIp, String clientName) {
		Category category = findActiveEntity(id);
		category.setDeletedYn(DELETED);
		HttpRequestUtils.applyUpdateAudit(category, clientIp, clientName);
		categoryRepository.save(category);
	}

	private Category findActiveEntity(Integer id) {
		return categoryRepository.findByIdAndDeletedYn(id, ACTIVE)
				.orElseThrow(() -> CategoryException.notFound("Category with id " + id + " was not found"));
	}

	private Sort buildSort(String orderBy) {
		if (!StringUtils.hasText(orderBy)) {
			return Sort.by(Sort.Direction.DESC, "createdAt");
		}

		String[] parts = orderBy.split(",");
		String property = parts[0].trim();
		String direction = parts.length > 1 ? parts[1].trim() : "DESC";

		if (!StringUtils.hasText(property)) {
			property = "createdAt";
		}

		Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direction)
				? Sort.Direction.ASC
				: Sort.Direction.DESC;

		return Sort.by(sortDirection, property);
	}
}
