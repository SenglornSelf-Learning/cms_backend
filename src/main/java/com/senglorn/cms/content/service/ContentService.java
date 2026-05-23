package com.senglorn.cms.content.service;

import java.util.List;

import com.senglorn.cms.model.CmsContent;

public interface ContentService {
    List<CmsContent> findContents();

    CmsContent findContentById(Integer id);

    CmsContent saveContent(CmsContent content);
}
