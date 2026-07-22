package com.cms.content.service;

import java.util.List;

import com.cms.content.dto.ContentDto.ContentRequest;
import com.cms.content.dto.ContentDto.ContentResponse;

public interface ContentService {

	List<ContentResponse> findAll();

	ContentResponse getById(Integer id);

	ContentResponse create(ContentRequest request, String clientIp, String clientName);

	ContentResponse update(Integer id, ContentRequest request, String clientIp, String clientName);

	void delete(Integer id, String clientIp, String clientName);
}
