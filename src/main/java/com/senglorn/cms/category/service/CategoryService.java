package com.senglorn.cms.category.service;

import java.util.List;

import org.springframework.lang.NonNull;

import com.senglorn.cms.category.entity.Category;

public interface CategoryService {
    List<Category> findCategories();

    Category findCategoryById(@NonNull Integer id);

    Category saveCategory(Category category);

    Category updateCategory(@NonNull Integer id, Category category);

    void deleteCategory(@NonNull Integer id);
}
