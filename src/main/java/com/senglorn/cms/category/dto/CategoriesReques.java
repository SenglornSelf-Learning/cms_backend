package com.senglorn.cms.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriesReques {
    @NotBlank(message = "Name is required")
    private String name;

    private Boolean isDeleted;
}
