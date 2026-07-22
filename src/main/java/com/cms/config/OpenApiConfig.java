package com.cms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI cmsOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("CMS API")
						.description("REST API for Content Management System")
						.version("1.0"));
	}
}
