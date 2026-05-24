package com.senglorn.cms.category.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.senglorn.cms.category.dto.CategoriesReques;
import com.senglorn.cms.category.dto.CategoriesRespone;
import com.senglorn.cms.category.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoriesRespone toResponse(Category category);

    List<CategoriesRespone> toResponseList(List<Category> categories);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoriesReques categoriesReques);
}
