package com.cms.userManagement.model;

import java.util.Locale;

import com.cms.globleException.exception.UserException;

public enum RoleName {
	ADMIN,
	EDITOR,
	SUBSCRIBER;

	public static RoleName from(String value) {
		if (value == null || value.isBlank()) {
			throw UserException.badRequest("Role must be ADMIN, EDITOR, or SUBSCRIBER");
		}
		try {
			return RoleName.valueOf(value.trim().toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException ex) {
			throw UserException.badRequest("Role must be ADMIN, EDITOR, or SUBSCRIBER");
		}
	}
}
