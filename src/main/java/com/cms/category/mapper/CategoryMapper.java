package com.cms.category.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.cms.category.model.Category;
import com.cms.category.dto.CategoryDto.CategoryRequest;
import com.cms.category.dto.CategoryDto.CategoryResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

	CategoryResponse toResponse(Category category);

	List<CategoryResponse> toResponseList(List<Category> categories);

	@Mapping(target = "id", ignore = true)
	Category toEntity(CategoryRequest request);

	@Mapping(target = "id", ignore = true)
	void updateEntity(CategoryRequest request, @MappingTarget Category category);
}
