package com.cms.content.service;

import java.util.List;

import com.cms.common.model.CmsContent;

public interface ContentService {
    List<CmsContent> findContents();

    CmsContent findContentById(Integer id);

    CmsContent saveContent(CmsContent content);

    CmsContent updateContent(Integer id, CmsContent content);

    boolean deleteContent(Integer id);
}
