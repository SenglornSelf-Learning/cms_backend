package com.cms.category.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cms.category.dto.CategoriesReques;
import com.cms.category.dto.CategoriesRespone;
import com.cms.category.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoriesRespone toResponse(Category category);

    List<CategoriesRespone> toResponseList(List<Category> categories);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoriesReques categoriesReques);
}
