package com.cms.globleException.exception;

import org.springframework.http.HttpStatus;

public class ContentException extends RuntimeException {

	private final HttpStatus status;

	public ContentException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}

	public ContentException(HttpStatus status, String message, Throwable cause) {
		super(message, cause);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public static ContentException badRequest(String message) {
		return new ContentException(HttpStatus.BAD_REQUEST, message);
	}

	public static ContentException notFound(String message) {
		return new ContentException(HttpStatus.NOT_FOUND, message);
	}

	public static ContentException forbidden(String message) {
		return new ContentException(HttpStatus.FORBIDDEN, message);
	}

	public static ContentException internalServerError(String message, Throwable cause) {
		return new ContentException(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
	}
}
