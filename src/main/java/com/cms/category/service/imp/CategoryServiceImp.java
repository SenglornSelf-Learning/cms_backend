package com.cms.category.service.imp;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.cms.category.entity.Category;
import com.cms.category.exception.CategoryNotFoundException;
import com.cms.category.repo.CategoryRepository;
import com.cms.category.service.CategoryService;
import com.cms.common.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> findCategories() {
        return categoryRepository.findAllByOrderByIdAsc();
    }

    @Override
    public Category findCategoryById(@NonNull Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    public Category saveCategory(Category category) {
        if (category.getName() == null) {
            throw new BadRequestException("Category name is required");
        }
 
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(@NonNull Integer id, Category category) {
        Category current = findCategoryById(id);
        category.setId(id);
        if (category.getIsDeleted() == null) {
            category.setIsDeleted(current.getIsDeleted());
        }
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(@NonNull Integer id) {
        Category current = findCategoryById(id);
        current.setIsDeleted(true);
        categoryRepository.save(current);
    }
}
