package com.senglorn.cms.category;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senglorn.cms.category.dto.CategoriesReques;
import com.senglorn.cms.category.dto.CategoriesRespone;
import com.senglorn.cms.category.entity.Category;
import com.senglorn.cms.category.mapper.CategoryMapper;
import com.senglorn.cms.category.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Tag(name = "Categories")
public class CategorytController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List categories")
    public List<CategoriesRespone> list() {
        List<Category> categories = categoryService.findCategories();
        return categoryMapper.toResponseList(categories);
    }

    @GetMapping(value = "/getById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get category by id")
    public ResponseEntity<CategoriesRespone> getById(@PathVariable("id") Integer id) {
        Category category = categoryService.findCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        CategoriesRespone response = categoryMapper.toResponse(category);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create category")
    public ResponseEntity<CategoriesRespone> create(@Valid @RequestBody CategoriesReques categoriesReques) {
        Category category = categoryMapper.toEntity(categoriesReques);
        Category saved = categoryService.saveCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toResponse(saved));
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update category")
    public ResponseEntity<CategoriesRespone> update(
            @PathVariable("id") Integer id,
            @Valid @RequestBody CategoriesReques categoriesReques) {
        Category category = categoryMapper.toEntity(categoriesReques);
        Category saved = categoryService.updateCategory(id, category);
        
        if (saved == null) {
            return ResponseEntity.notFound().build();
        }
        CategoriesRespone response = categoryMapper.toResponse(saved);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
