package com.cms.dashboard.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cms.common.response.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard")
public class DashboardController {

	@GetMapping
	@Operation(summary = "Dashboard metadata for the admin UI")
	public ResponseBody<Map<String, String>> summary() {
		return ResponseBody.ok("Success", Map.of(
				"title", "CMS Admin",
				"description", "Vue + Spring Boot CMS"));
	}
}
