package com.cms.common.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CmsContent {
    private Integer id;
    private String uuid;
    private String slug;
    private String keyword;
    private String title;
    private String description;
    private String thumbnail;
    private String editor;
    private Boolean isDeleted;
    private LocalDateTime createAt;
    private Integer categoryId;
}
