package com.cms.content.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.cms.content.dto.ContentDto.ContentRequest;
import com.cms.content.dto.ContentDto.ContentResponse;
import com.cms.content.model.Content;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContentMapper {

	@Mapping(target = "categoryId", source = "category.id")
	@Mapping(target = "thumbnails", ignore = true)
	ContentResponse toResponse(Content content);

	List<ContentResponse> toResponseList(List<Content> contents);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "category", ignore = true)
	Content toEntity(ContentRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "category", ignore = true)
	void updateEntity(ContentRequest request, @MappingTarget Content content);
}
