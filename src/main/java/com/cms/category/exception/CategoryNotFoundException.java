package com.cms.category.exception;

import com.cms.common.exception.ResourceNotFoundException;

public class CategoryNotFoundException extends ResourceNotFoundException {

    public CategoryNotFoundException(Integer id) {
        super("Category with id " + id + " was not found");
    }
}
