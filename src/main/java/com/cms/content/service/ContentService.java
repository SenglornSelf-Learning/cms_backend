package com.cms.content.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.cms.common.response.PageResponse;
import com.cms.content.dto.ContentDto.ContentRequest;
import com.cms.content.dto.ContentDto.ContentResponse;
import com.cms.content.dto.ContentDto.ThumbnailDownload;

public interface ContentService {

	PageResponse<ContentResponse> findAll(Integer pageIndex, Integer pageSize, String orderBy, String title, String editor);

	ContentResponse getById(Integer id);

	ContentResponse create(ContentRequest request, List<MultipartFile> files, String clientIp, String clientName);

	ContentResponse update(
			Integer id,
			ContentRequest request,
			List<MultipartFile> files,
			List<Integer> deletedThumbnailIds,
			String clientIp,
			String clientName);

	void delete(Integer id, String clientIp, String clientName);

	ThumbnailDownload getThumbnailDownload(Integer contentId, Integer fileId);
}
