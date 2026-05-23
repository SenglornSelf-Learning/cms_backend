package com.senglorn.cms.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Category {
    private Integer id;
    private String name;
    private Boolean isDeleted;
}
