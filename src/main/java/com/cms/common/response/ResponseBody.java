package com.cms.common.response;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseBody<T> {

	private boolean status;
	private int statusCode;
	private String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;

	public ResponseBody(boolean status, int statusCode, String message) {
		this.status = status;
		this.statusCode = statusCode;
		this.message = message;
	}

	public static <T> ResponseBody<T> ok(String message, T data) {
		ResponseBody<T> body = new ResponseBody<>();
		body.status = true;
		body.statusCode = HttpStatus.OK.value();
		body.message = message;
		body.data = data;
		return body;
	}

	public static <T> ResponseBody<T> error(HttpStatus httpStatus, String message) {
		ResponseBody<T> body = new ResponseBody<>();
		body.status = false;
		body.statusCode = httpStatus.value();
		body.message = message;
		return body;
	}
}
