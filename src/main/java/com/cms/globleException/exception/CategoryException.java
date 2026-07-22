package com.cms.globleException.exception;

import org.springframework.http.HttpStatus;

public class CategoryException extends RuntimeException {

	private final HttpStatus status;

	public CategoryException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}

	public CategoryException(HttpStatus status, String message, Throwable cause) {
		super(message, cause);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public static CategoryException badRequest(String message) {
		return new CategoryException(HttpStatus.BAD_REQUEST, message);
	}

	public static CategoryException notFound(String message) {
		return new CategoryException(HttpStatus.NOT_FOUND, message);
	}

	public static CategoryException forbidden(String message) {
		return new CategoryException(HttpStatus.FORBIDDEN, message);
	}

	public static CategoryException internalServerError(String message, Throwable cause) {
		return new CategoryException(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
	}
}
