package com.senglorn.cms.category.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.senglorn.cms.category.CategoryRepository;
import com.senglorn.cms.model.Category;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService{

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> findCategories() {
        return categoryRepository.selectAllCategorise();
    }

    @Override
    public Category findCategoryById(Integer id) {
        return categoryRepository.selectCategoryById(id);
    }

    @Override
    public Category saveCategory(Category category) {
        if (category.getIsDeleted() == null) {
            category.setIsDeleted(false);
        }
        categoryRepository.insertCategory(category);
        
        return category;
    }
}
