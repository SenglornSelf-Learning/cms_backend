package com.cms.content.service.impl;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.cms.category.repository.CategoryRepository;
import com.cms.common.web.HttpRequestUtils;
import com.cms.content.dto.ContentDto.ContentRequest;
import com.cms.content.dto.ContentDto.ContentResponse;
import com.cms.content.mapper.ContentMapper;
import com.cms.content.model.Content;
import com.cms.content.repository.ContentRepository;
import com.cms.content.service.ContentService;
import com.cms.globleException.exception.CategoryException;
import com.cms.globleException.exception.ContentException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentServiceImpl implements ContentService {

	private static final String ACTIVE = "N";
	private static final String DELETED = "Y";

	private final ContentRepository contentRepository;
	private final CategoryRepository categoryRepository;
	private final ContentMapper contentMapper;

	@Override
	public List<ContentResponse> findAll() {
		return contentMapper.toResponseList(contentRepository.findAllByOrderByIdAsc());
	}

	@Override
	public ContentResponse getById(Integer id) {
		return contentMapper.toResponse(findActiveEntity(id));
	}

	@Override
	@Transactional
	public ContentResponse create(ContentRequest request, String clientIp, String clientName) {
		Content entity = contentMapper.toEntity(request);
		if (request.getCategoryId() == null) {
			throw ContentException.badRequest("Category is required");
		}
		entity.setCategory(categoryRepository.findByIdAndDeletedYn(request.getCategoryId(), ACTIVE)
				.orElseThrow(() -> CategoryException.notFound(
						"Category with id " + request.getCategoryId() + " was not found")));
		if (!StringUtils.hasText(entity.getUuid())) {
			entity.setUuid(UUID.randomUUID().toString());
		}
		if (!StringUtils.hasText(entity.getSlug())) {
			entity.setSlug(toSlug(entity.getTitle()));
		}
		entity.setDeletedYn(ACTIVE);
		HttpRequestUtils.applyCreateAudit(entity, clientIp, clientName);
		Content saved = contentRepository.save(entity);
		return contentMapper.toResponse(saved);
	}

	@Override
	@Transactional
	public ContentResponse update(Integer id, ContentRequest request, String clientIp, String clientName) {
		Content content = findActiveEntity(id);
		String previousUuid = content.getUuid();
		contentMapper.updateEntity(request, content);
		if (request.getCategoryId() == null) {
			throw ContentException.badRequest("Category is required");
		}
		content.setCategory(categoryRepository.findByIdAndDeletedYn(request.getCategoryId(), ACTIVE)
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

	@Override
	@Transactional
	public void delete(Integer id, String clientIp, String clientName) {
		Content content = findActiveEntity(id);
		content.setDeletedYn(DELETED);
		HttpRequestUtils.applyUpdateAudit(content, clientIp, clientName);
		contentRepository.save(content);
	}

	private Content findActiveEntity(Integer id) {
		return contentRepository.findByIdAndDeletedYn(id, ACTIVE)
				.orElseThrow(() -> ContentException.notFound("Content with id " + id + " was not found"));
	}

	private String toSlug(String value) {
		String source = StringUtils.hasText(value) ? value : UUID.randomUUID().toString();
		String normalized = Normalizer.normalize(source, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "");
		String slug = normalized.toLowerCase(Locale.ROOT)
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("(^-|-$)", "");
		return slug.isBlank() ? UUID.randomUUID().toString() : slug;
	}
}
