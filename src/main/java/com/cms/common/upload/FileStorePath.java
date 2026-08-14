package com.cms.common.upload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class FileStorePath {

	@Value("${cms.file-store-path}")
	private String fileStorePath;

	@PostConstruct
	public void ensureRootDirectoryExists() {
		ensureDirectoryExists(rootPath());
	}

	public Path rootPath() {
		return Path.of(fileStorePath).toAbsolutePath().normalize();
	}

	public void ensureDirectoryExists(Path directory) {
		try {
			Files.createDirectories(directory);
		} catch (IOException ex) {
			throw new IllegalStateException("Unable to create file store directory: " + directory, ex);
		}
	}
}
