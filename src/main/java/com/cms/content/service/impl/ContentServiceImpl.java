package com.cms.content.service.impl;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.cms.category.repository.CategoryRepository;
import com.cms.common.response.PageResponse;
import com.cms.common.web.HttpRequestUtils;
import com.cms.content.dto.ContentDto.ContentRequest;
import com.cms.content.dto.ContentDto.ContentResponse;
import com.cms.content.mapper.ContentMapper;
import com.cms.content.model.Content;
import com.cms.content.repository.ContentRepository;
import com.cms.content.service.ContentService;
import com.cms.globleException.exception.CategoryException;
import com.cms.globleException.exception.ContentException;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentServiceImpl implements ContentService {

	private static final String NOT_DELETED = "N";
	private static final String DELETED = "Y";

	private final ContentRepository contentRepository;
	private final ContentMapper contentMapper;
	private final CategoryRepository categoryRepository;

	// create content
	@Override
	@Transactional
	public ContentResponse create(ContentRequest request, String clientIp, String clientName) {
		Content content = contentMapper.toEntity(request);
		if (request.getCategoryId() == null) {
			throw ContentException.badRequest("Category ID is required");
		}
		content.setCategory(categoryRepository.findByIdAndDeletedYn(request.getCategoryId(), NOT_DELETED)
				.orElseThrow(() -> CategoryException.notFound(
						"Category with id " + request.getCategoryId() + " was not found")));

		if (!StringUtils.hasText(content.getUuid())) {
			content.setUuid(UUID.randomUUID().toString());
		}
		if (!StringUtils.hasText(content.getSlug())) {
			content.setSlug(toSlug(content.getTitle()));
		}
		content.setDeletedYn(NOT_DELETED);
		HttpRequestUtils.applyCreateAudit(content, clientIp, clientName);
		Content saved = contentRepository.save(content);
		return contentMapper.toResponse(saved);
	}

	// find all contents
	@Override
	public PageResponse<ContentResponse> findAll(Integer pageIndex, Integer pageSize, String orderBy, String title, String editor) {
		int setPageIndex = pageIndex == null || pageIndex < 1 ? 1 : pageIndex;
		int setPageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
		Sort sort = buildSort(orderBy);

		Pageable pageable = PageRequest.of(setPageIndex - 1, setPageSize, sort);
		Page<Content> contents = contentRepository.findAll(buildContentFilter(title, editor), pageable);
		return new PageResponse<>(
			contentMapper.toResponseList(
				contents.getContent()),
				contents.getTotalElements(),
				setPageIndex,
				setPageSize,
				contents.getTotalPages()
			);
	}

	// find content by id
	@Override
	public ContentResponse getById(Integer id) {
		return contentMapper.toResponse(findContentById(id));
	}

	// update content
	@Override
	@Transactional
	public ContentResponse update(Integer id, ContentRequest request, String clientIp, String clientName) {
		Content content = findContentById(id);
		String previousUuid = content.getUuid();
		contentMapper.updateEntity(request, content);
		if (request.getCategoryId() == null) {
			throw ContentException.badRequest("Category is required");
		}
		content.setCategory(categoryRepository.findByIdAndDeletedYn(request.getCategoryId(), NOT_DELETED)
				.orElseThrow(() -> CategoryException.notFound(
						"Category with id " + request.getCategoryId() + " was not found")));
		if (!StringUtils.hasText(content.getUuid())) {
			content.setUuid(previousUuid);
		}
		if (!StringUtils.hasText(content.getSlug())) {
			content.setSlug(toSlug(content.getTitle()));
		}
		HttpRequestUtils.applyUpdateAudit(content, clientIp, clientName);
		Content saved = contentRepository.save(content);
		return contentMapper.toResponse(saved);
	}

	// delete content
	@Override
	@Transactional
	public void delete(Integer id, String clientIp, String clientName) {
		Content content = findContentById(id);
		content.setDeletedYn(DELETED);

		HttpRequestUtils.applyUpdateAudit(content, clientIp, clientName);
		contentRepository.save(content);
	}

	// find content by id
	private Content findContentById(Integer id) {
		return contentRepository.findByIdAndDeletedYn(id, NOT_DELETED)
				.orElseThrow(() -> ContentException.notFound("Content with id " + id + " was not found"));
	}

	// convert title to slug
	private String toSlug(String value) {
		String source = StringUtils.hasText(value) ? value : UUID.randomUUID().toString();
		String normalized = Normalizer.normalize(source, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "");
		String slug = normalized.toLowerCase(Locale.ROOT)
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("(^-|-$)", "");
		return slug.isBlank() ? UUID.randomUUID().toString() : slug;
	}

	// sort by createdAt, DESC by default
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

	// filter category
	private Specification<Content> buildContentFilter(String title, String editor) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.equal(root.get("deletedYn"), NOT_DELETED));

			if (StringUtils.hasText(title)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.trim().toLowerCase() + "%"));
			}
			if (StringUtils.hasText(editor)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("editor")), "%" + editor.trim().toLowerCase() + "%"));
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
