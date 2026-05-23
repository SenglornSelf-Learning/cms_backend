package com.senglorn.cms.category.service;

import java.util.List;

import com.senglorn.cms.model.Category;

public interface CategoryService {
    List<Category> findCategories();

    Category findCategoryById(Integer id);

    Category saveCategory(Category category);
}
