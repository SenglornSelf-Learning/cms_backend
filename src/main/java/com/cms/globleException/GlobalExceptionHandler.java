package com.cms.globleException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import com.cms.common.response.ResponseBody;
import com.cms.globleException.exception.CategoryException;
import com.cms.globleException.exception.ContentException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CategoryException.class)
	public ResponseEntity<ResponseBody<Void>> handleCategory(CategoryException ex) {
		return ResponseEntity.status(ex.getStatus())
				.body(ResponseBody.error(ex.getStatus(), ex.getMessage()));
	}

	@ExceptionHandler(ContentException.class)
	public ResponseEntity<ResponseBody<Void>> handleContent(ContentException ex) {
		return ResponseEntity.status(ex.getStatus())
				.body(ResponseBody.error(ex.getStatus(), ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseBody<Void>> handleValidation(MethodArgumentNotValidException ex) {
		return validationResponse(ex.getBindingResult().getFieldErrors());
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ResponseBody<Void>> handleBindValidation(BindException ex) {
		return validationResponse(ex.getBindingResult().getFieldErrors());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ResponseBody<Void>> handleConstraintViolation(ConstraintViolationException ex) {
		String message = ex.getConstraintViolations().stream()
				.map(GlobalExceptionHandler::toReadableConstraintMessage)
				.findFirst()
				.orElse("Validation failed.");
		return ResponseEntity.badRequest()
				.body(ResponseBody.error(HttpStatus.BAD_REQUEST, message));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ResponseBody<Void>> handleIllegalArgument(IllegalArgumentException ex) {
		log.warn("Invalid argument", ex);
		return ResponseEntity.badRequest()
				.body(ResponseBody.error(HttpStatus.BAD_REQUEST, toReadableMessage(ex.getMessage())));
	}

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ResponseBody<Void>> handleNullPointer(NullPointerException ex) {
		log.error("Null pointer exception", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ResponseBody.error(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"A required field was missing during processing."));
	}

	// JSON format error as a 400(type mismatch during execution).
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseBody<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		return ResponseEntity.badRequest()
				.body(ResponseBody.error(HttpStatus.BAD_REQUEST, "Invalid JSON format."));
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ResponseBody<Void>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
		log.warn("Upload size exceeded", ex);
		return ResponseEntity.badRequest()
				.body(ResponseBody.error(HttpStatus.BAD_REQUEST, "The selected file exceeds the maximum file size of 5 MB."));
	}

	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<ResponseBody<Void>> handleMultipart(MultipartException ex) {
		log.warn("Multipart upload failed", ex);
		if (ex.getCause() instanceof MaxUploadSizeExceededException) {
			return handleMaxUploadSize((MaxUploadSizeExceededException) ex.getCause());
		}
		return ResponseEntity.badRequest()
				.body(ResponseBody.error(HttpStatus.BAD_REQUEST, "Invalid file upload."));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseBody<Void>> handleGeneric(Exception ex) {
		log.error("Unhandled exception", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ResponseBody.error(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"An error occurred during execution."));
	}

	private static ResponseEntity<ResponseBody<Void>> validationResponse(Iterable<FieldError> fieldErrors) {
		for (FieldError fieldError : fieldErrors) {
			String message = toReadableMessage(fieldError.getDefaultMessage());
			if (!hasText(message)) {
				message = fieldError.getField() + " is invalid.";
			}
			return ResponseEntity.badRequest()
					.body(ResponseBody.error(HttpStatus.BAD_REQUEST, message));
		}
		return ResponseEntity.badRequest()
				.body(ResponseBody.error(HttpStatus.BAD_REQUEST, "Validation failed."));
	}

	private static String toReadableMessage(String message) {
		if (!hasText(message)) {
			return null;
		}
		String trimmed = message.trim();
		if (trimmed.endsWith("Exception") || trimmed.contains("NullPointer")) {
			return "An error occurred during execution.";
		}
		return trimmed;
	}

	private static String toReadableConstraintMessage(ConstraintViolation<?> violation) {
		String message = violation.getMessage();
		if (hasText(message) && !message.equals("must not be blank")) {
			return message.trim();
		}
		return violation.getPropertyPath() + " is required.";
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}
}
