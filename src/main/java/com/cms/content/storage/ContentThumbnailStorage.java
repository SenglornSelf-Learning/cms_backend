package com.cms.content.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cms.common.upload.FileStorePath;
import com.cms.common.upload.FileUploadValidate;
import com.cms.content.model.Content;
import com.cms.content.model.ContentThumbnail;
import com.cms.globleException.exception.ContentException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContentThumbnailStorage {

	private final FileUploadValidate fileUploadValidate;
	private final FileStorePath fileStore;

	public ContentThumbnail store(Content content, MultipartFile file, String clientIp) {
		if (content == null || content.getId() == null) {
			throw ContentException.badRequest("Content must be saved before storing thumbnails.");
		}

		fileUploadValidate.validate(file);

		String originalFileName = cleanOriginalFileName(file.getOriginalFilename());
		String storedFileName = UUID.randomUUID().toString().replace("-", "") + extension(originalFileName);
		Path rootPath = fileStore.rootPath();
		Path contentPath = rootPath
				.resolve("contents")
				.resolve(String.valueOf(LocalDate.now().getYear()))
				.resolve(String.valueOf(content.getId()))
				.normalize();
		Path targetPath = contentPath.resolve(storedFileName).normalize();
		if (!contentPath.startsWith(rootPath) || !targetPath.startsWith(rootPath)) {
			throw ContentException.badRequest("Invalid upload file path.");
		}

		writeUploadedFile(file, contentPath, targetPath);

		ContentThumbnail thumbnail = new ContentThumbnail();
		thumbnail.setContent(content);
		thumbnail.setStoredFileName(storedFileName);
		thumbnail.setOriginalFileName(originalFileName);
		thumbnail.setContentType(file.getContentType());
		thumbnail.setFileSize(file.getSize());
		thumbnail.setFilePath(targetPath.toString());
		thumbnail.setCreatedIp(clientIp);
		return thumbnail;
	}

	private void writeUploadedFile(MultipartFile file, Path parentPath, Path targetPath) {
		fileStore.ensureDirectoryExists(parentPath);
		try (InputStream inputStream = file.getInputStream()) {
			Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			throw ContentException.badRequest(
					"Unable to upload the file. Please contact the administrator if the issue persists.");
		}
	}

	private static String cleanOriginalFileName(String originalFileName) {
		if (originalFileName == null || originalFileName.isBlank()) {
			return "upload";
		}
		return Path.of(originalFileName).getFileName().toString();
	}

	private static String extension(String fileName) {
		int dot = fileName.lastIndexOf('.');
		if (dot < 0 || dot == fileName.length() - 1) {
			return "";
		}
		return fileName.substring(dot);
	}
}
