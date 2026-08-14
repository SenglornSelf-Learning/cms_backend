package com.cms.content.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cms.category.repository.CategoryRepository;
import com.cms.common.response.PageResponse;
import com.cms.common.web.HttpRequestUtils;
import com.cms.content.dto.ContentDto.ContentRequest;
import com.cms.content.dto.ContentDto.ContentResponse;
import com.cms.content.dto.ContentDto.ThumbnailDownload;
import com.cms.content.dto.ContentDto.ThumbnailResponse;
import com.cms.content.mapper.ContentMapper;
import com.cms.content.model.Content;
import com.cms.content.model.ContentThumbnail;
import com.cms.content.repository.ContentRepository;
import com.cms.content.repository.ContentThumbnailRepository;
import com.cms.content.service.ContentService;
import com.cms.content.storage.ContentThumbnailStorage;
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
	private final ContentThumbnailRepository thumbnailRepository;
	private final ContentThumbnailStorage thumbnailStorage;
	private final ContentMapper contentMapper;
	private final CategoryRepository categoryRepository;

	@Override
	@Transactional
	public ContentResponse create(ContentRequest request, List<MultipartFile> files, String clientIp, String clientName) {
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
		List<ContentThumbnail> thumbnails = saveNewFiles(saved, files, clientIp, clientName);
		return toResponse(saved, thumbnails);
	}

	@Override
	public PageResponse<ContentResponse> findAll(Integer pageIndex, Integer pageSize, String orderBy, String title, String editor) {
		int setPageIndex = pageIndex == null || pageIndex < 1 ? 1 : pageIndex;
		int setPageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
		Sort sort = buildSort(orderBy);

		Pageable pageable = PageRequest.of(setPageIndex - 1, setPageSize, sort);
		Page<Content> contents = contentRepository.findAll(buildContentFilter(title, editor), pageable);
		List<Content> rows = contents.getContent();
		Map<Integer, List<ThumbnailResponse>> thumbnailsByContent = thumbnailsByContent(rows);
		List<ContentResponse> payload = contentMapper.toResponseList(rows);
		for (ContentResponse response : payload) {
			response.setThumbnails(thumbnailsByContent.getOrDefault(response.getId(), List.of()));
		}
		return new PageResponse<>(
				payload,
				contents.getTotalElements(),
				setPageIndex,
				setPageSize,
				contents.getTotalPages());
	}

	@Override
	public ContentResponse getById(Integer id) {
		Content content = findContentById(id);
		return toResponse(content, activeFiles(id));
	}

	@Override
	@Transactional
	public ContentResponse update(
			Integer id,
			ContentRequest request,
			List<MultipartFile> files,
			List<Integer> deletedThumbnailIds,
			String clientIp,
			String clientName) {
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
		softDeleteFiles(saved, deletedThumbnailIds, clientIp, clientName);
		saveNewFiles(saved, files, clientIp, clientName);
		return toResponse(saved, activeFiles(saved.getId()));
	}

	@Override
	@Transactional
	public void delete(Integer id, String clientIp, String clientName) {
		Content content = findContentById(id);
		content.setDeletedYn(DELETED);
		HttpRequestUtils.applyUpdateAudit(content, clientIp, clientName);
		contentRepository.save(content);
		softDeleteContentFiles(id, clientIp, clientName);
	}

	@Override
	public ThumbnailDownload getThumbnailDownload(Integer contentId, Integer fileId) {
		findContentById(contentId);
		ContentThumbnail file = thumbnailRepository.findByIdAndContentIdAndDeletedYn(fileId, contentId, NOT_DELETED)
				.orElseThrow(() -> ContentException.notFound("Thumbnail was not found"));
		Path filePath = Path.of(file.getFilePath()).toAbsolutePath().normalize();
		if (!Files.isRegularFile(filePath)) {
			throw ContentException.notFound("Thumbnail file was not found on disk");
		}
		return new ThumbnailDownload(
				file.getOriginalFileName(),
				file.getContentType(),
				file.getFileSize(),
				new FileSystemResource(filePath));
	}

	private Content findContentById(Integer id) {
		return contentRepository.findByIdAndDeletedYn(id, NOT_DELETED)
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

	private List<ContentThumbnail> saveNewFiles(
			Content content,
			List<MultipartFile> files,
			String clientIp,
			String clientName) {
		List<ContentThumbnail> thumbnails = nonEmptyFiles(files).stream()
				.map(file -> {
					ContentThumbnail stored = thumbnailStorage.store(content, file, clientIp);
					HttpRequestUtils.applyCreateAudit(stored, clientIp, clientName);
					stored.setDeletedYn(NOT_DELETED);
					return stored;
				})
				.toList();
		if (thumbnails.isEmpty()) {
			return List.of();
		}
		return thumbnailRepository.saveAll(thumbnails);
	}

	private void softDeleteFiles(
			Content content,
			List<Integer> fileIds,
			String clientIp,
			String clientName) {
		if (fileIds == null || fileIds.isEmpty()) {
			return;
		}
		List<ContentThumbnail> files = thumbnailRepository.findByContentIdAndIdInAndDeletedYn(
				content.getId(), fileIds, NOT_DELETED);
		for (ContentThumbnail file : files) {
			file.setDeletedYn(DELETED);
			HttpRequestUtils.applyUpdateAudit(file, clientIp, clientName);
		}
		if (!files.isEmpty()) {
			thumbnailRepository.saveAll(files);
		}
	}

	private void softDeleteContentFiles(Integer contentId, String clientIp, String clientName) {
		List<ContentThumbnail> files = activeFiles(contentId);
		for (ContentThumbnail file : files) {
			file.setDeletedYn(DELETED);
			HttpRequestUtils.applyUpdateAudit(file, clientIp, clientName);
		}
		if (!files.isEmpty()) {
			thumbnailRepository.saveAll(files);
		}
	}

	private List<ContentThumbnail> activeFiles(Integer contentId) {
		return thumbnailRepository.findByContentIdAndDeletedYnOrderByIdAsc(contentId, NOT_DELETED);
	}

	private Map<Integer, List<ThumbnailResponse>> thumbnailsByContent(List<Content> contents) {
		List<Integer> ids = contents.stream().map(Content::getId).toList();
		if (ids.isEmpty()) {
			return Map.of();
		}
		return thumbnailRepository.findByContentIdInAndDeletedYnOrderByContentIdAscIdAsc(ids, NOT_DELETED).stream()
				.collect(Collectors.groupingBy(
						file -> file.getContent().getId(),
						Collectors.mapping(this::toThumbnailResponse, Collectors.toList())));
	}

	private ContentResponse toResponse(Content content, List<ContentThumbnail> thumbnails) {
		ContentResponse response = contentMapper.toResponse(content);
		response.setThumbnails(thumbnails.stream().map(this::toThumbnailResponse).toList());
		return response;
	}

	private ThumbnailResponse toThumbnailResponse(ContentThumbnail file) {
		return new ThumbnailResponse(
				file.getId(),
				file.getOriginalFileName(),
				file.getContentType(),
				file.getFileSize(),
				thumbnailUrl(file.getContent().getId(), file.getId()));
	}

	private static String thumbnailUrl(Integer contentId, Integer fileId) {
		return "/api/contents/" + contentId + "/thumbnails/" + fileId;
	}

	private static List<MultipartFile> nonEmptyFiles(List<MultipartFile> files) {
		if (files == null || files.isEmpty()) {
			return List.of();
		}
		return files.stream().filter(Objects::nonNull).filter(file -> !file.isEmpty()).toList();
	}
}
