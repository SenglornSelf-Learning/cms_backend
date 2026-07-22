package com.cms.common.web;

import java.util.Locale;

import com.cms.common.model.BaseEntity;

import jakarta.servlet.http.HttpServletRequest;

public final class HttpRequestUtils {

	public static final String CLIENT_NAME_HEADER = "Client-Name";

	private HttpRequestUtils() {}

	public static String clientIp(HttpServletRequest request) {
		String ip = firstHeaderValue(request, "X-Forwarded-For");
		if (ip == null) {
			ip = firstHeaderValue(request, "X-Real-IP");
		}
		if (ip == null || ip.isBlank()) {
			ip = request.getRemoteAddr();
		}
		return normalizeClientIp(ip);
	}

	public static String clientName(HttpServletRequest request) {
		String user = request.getHeader(CLIENT_NAME_HEADER);
		if (user != null && !user.isBlank()) {
			return user.trim();
		}
		return null;
	}

	public static void applyCreateAudit(BaseEntity entity, String clientIp, String clientName) {
		entity.setCreatedBy(clientName);
		entity.setCreatedIp(clientIp);
	}

	public static void applyUpdateAudit(BaseEntity entity, String clientIp, String clientName) {
		entity.setModifiedBy(clientName);
		entity.setModifiedIp(clientIp);
	}

	private static String firstHeaderValue(HttpServletRequest request, String headerName) {
		String value = request.getHeader(headerName);
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.split(",")[0].trim();
	}

	private static String normalizeClientIp(String ip) {
		if (ip == null) {
			return null;
		}
		String normalized = ip.trim();
		if (normalized.isBlank()) {
			return null;
		}
		String lower = normalized.toLowerCase(Locale.ROOT);
		if ("::1".equals(lower) || "0:0:0:0:0:0:0:1".equals(lower)) {
			return "127.0.0.1";
		}
		return normalized;
	}
}
