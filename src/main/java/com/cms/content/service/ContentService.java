package com.cms.content.service;

import com.cms.common.response.PageResponse;
import com.cms.content.dto.ContentDto.ContentRequest;
import com.cms.content.dto.ContentDto.ContentResponse;

public interface ContentService {

	PageResponse<ContentResponse> findAll(Integer pageIndex, Integer pageSize, String orderBy, String title, String editor);

	ContentResponse getById(Integer id);

	ContentResponse create(ContentRequest request, String clientIp, String clientName);

	ContentResponse update(Integer id, ContentRequest request, String clientIp, String clientName);

	void delete(Integer id, String clientIp, String clientName);
}
