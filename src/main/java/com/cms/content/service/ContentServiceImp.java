package com.cms.content.service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cms.common.model.CmsContent;
import com.cms.content.ContentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentServiceImp implements ContentService {

    private final ContentRepository contentRepository;

    @Override
    public List<CmsContent> findContents() {
        return contentRepository.selectAllContents();
    }

    @Override
    public CmsContent findContentById(Integer id) {
        return contentRepository.selectContentById(id);
    }

    @Override
    public CmsContent saveContent(CmsContent content) {
        prepareForCreate(content);
        contentRepository.insertContent(content);
        return content;
    }

    @Override
    public CmsContent updateContent(Integer id, CmsContent content) {
        CmsContent current = contentRepository.selectContentById(id);
        if (current == null) {
            return null;
        }
        content.setId(id);
        prepareForUpdate(content, current);
        contentRepository.updateContent(content);
        return contentRepository.selectContentById(id);
    }

    @Override
    public boolean deleteContent(Integer id) {
        CmsContent current = contentRepository.selectContentById(id);
        if (current == null) {
            return false;
        }
        return contentRepository.softDeleteContent(id) > 0;
    }

    private void prepareForCreate(CmsContent content) {
        if (isBlank(content.getUuid())) {
            content.setUuid(UUID.randomUUID().toString());
        }
        if (isBlank(content.getSlug())) {
            content.setSlug(toSlug(content.getTitle()));
        }
        if (content.getIsDeleted() == null) {
            content.setIsDeleted(false);
        }
        if (content.getCreateAt() == null) {
            content.setCreateAt(LocalDateTime.now());
        }
    }

    private void prepareForUpdate(CmsContent content, CmsContent current) {
        content.setUuid(isBlank(content.getUuid()) ? current.getUuid() : content.getUuid());
        content.setSlug(isBlank(content.getSlug()) ? toSlug(content.getTitle()) : content.getSlug());
        content.setIsDeleted(content.getIsDeleted() == null ? current.getIsDeleted() : content.getIsDeleted());
        content.setCreateAt(content.getCreateAt() == null ? current.getCreateAt() : content.getCreateAt());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String toSlug(String value) {
        String source = isBlank(value) ? UUID.randomUUID().toString() : value;
        String normalized = Normalizer.normalize(source, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String slug = normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return slug.isBlank() ? UUID.randomUUID().toString() : slug;
    }
}
