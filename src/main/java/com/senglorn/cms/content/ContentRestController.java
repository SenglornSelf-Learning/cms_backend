package com.senglorn.cms.content;

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

import com.senglorn.cms.content.service.ContentService;
import com.senglorn.cms.model.CmsContent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
@Tag(name = "Contents")
public class ContentRestController {

    private final ContentService contentService;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List contents")
    public List<CmsContent> list() {
        return contentService.findContents();
    }

    @GetMapping(value = "/getById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get content by id")
    public ResponseEntity<CmsContent> getById(@PathVariable("id") Integer id) {
        CmsContent content = contentService.findContentById(id);
        if (content == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(content);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create content")
    public ResponseEntity<CmsContent> create(@RequestBody CmsContent content) {
        CmsContent saved = contentService.saveContent(content);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update content")
    public ResponseEntity<CmsContent> update(@PathVariable("id") Integer id, @RequestBody CmsContent content) {
        CmsContent saved = contentService.updateContent(id, content);
        if (saved == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete content")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        boolean deleted = contentService.deleteContent(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
