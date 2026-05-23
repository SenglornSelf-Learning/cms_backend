package com.senglorn.cms.content.service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.senglorn.cms.content.ContentRepository;
import com.senglorn.cms.model.CmsContent;

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

        contentRepository.insertContent(content);
        return content;
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
