package com.cms.common.upload;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import com.cms.globleException.exception.ContentException;

@Component
public class FileUploadValidate {

	@Value("${cms.file-upload.max-file-size}")
	private DataSize maxFileSize;

	@Value("${cms.file-upload.allowed-extensions}")
	private String allowedExtensions;

	@Value("${cms.file-upload.allowed-content-types}")
	private String allowedContentTypes;

	public void validate(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw ContentException.badRequest("Empty files cannot be uploaded.");
		}

		if (file.getSize() > maxFileSize.toBytes()) {
			throw ContentException.badRequest("The selected file exceeds the maximum file size of 5 MB.");
		}

		String extension = extension(file.getOriginalFilename());
		if (extension.isBlank() || !allowedExtensions().contains(extension)) {
			throw ContentException.badRequest(
					"The %s file extension is not allowed. Allowed: jpg, jpeg, png, gif, webp."
							.formatted(extension.isBlank() ? "missing" : extension.toUpperCase(Locale.ROOT)));
		}

		String contentType = normalizeContentType(file.getContentType());
		if (StringUtils.hasText(contentType) && !allowedContentTypes().contains(contentType)) {
			throw ContentException.badRequest(
					"The file type is not allowed. Allowed: jpg, jpeg, png, gif, webp.");
		}
	}

	private Set<String> allowedExtensions() {
		return csvSet(allowedExtensions);
	}

	private Set<String> allowedContentTypes() {
		return csvSet(allowedContentTypes);
	}

	private static Set<String> csvSet(String csv) {
		return Arrays.stream(csv.split(","))
				.map(String::trim)
				.filter(value -> !value.isBlank())
				.map(value -> value.toLowerCase(Locale.ROOT))
				.collect(Collectors.toUnmodifiableSet());
	}

	private static String normalizeContentType(String contentType) {
		if (!StringUtils.hasText(contentType)) {
			return "";
		}
		String normalized = contentType.trim().toLowerCase(Locale.ROOT);
		int separator = normalized.indexOf(';');
		if (separator >= 0) {
			normalized = normalized.substring(0, separator).trim();
		}
		if ("image/jpg".equals(normalized)) {
			return "image/jpeg";
		}
		return normalized;
	}

	private static String extension(String fileName) {
		String cleaned = cleanOriginalFileName(fileName);
		int dot = cleaned.lastIndexOf('.');
		if (dot < 0 || dot == cleaned.length() - 1) {
			return "";
		}
		return cleaned.substring(dot + 1).toLowerCase(Locale.ROOT);
	}

	private static String cleanOriginalFileName(String originalFileName) {
		if (originalFileName == null || originalFileName.isBlank()) {
			return "upload";
		}
		return Path.of(originalFileName).getFileName().toString();
	}
}
