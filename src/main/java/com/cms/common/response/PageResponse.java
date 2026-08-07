package com.cms.common.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageResponse<T> {

	private boolean status;
	private int statusCode;
	private String message;

	private List<T> payload;
	private long totalCount;
	private int pageIndex;
	private int pageSize;
	private int totalPages;

	public PageResponse(List<T> payload, long totalCount, int pageIndex, int pageSize, int totalPages) {
		this.status = true;
		this.statusCode = 200;
		this.message = "Success";
		this.payload = payload;
		this.totalCount = totalCount;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.totalPages = totalPages;
	}

	public static <T> PageResponse<T> ok(String message, List<T> payload) {
		PageResponse<T> r = new PageResponse<>();
		r.status = true;
		r.statusCode = 200;
		r.message = message;
		r.payload = payload;
		r.totalCount = payload.size();
		r.pageIndex = 0;
		r.pageSize = payload.size();
		r.totalPages = payload.isEmpty() ? 0 : 1;
		return r;
	}
}
