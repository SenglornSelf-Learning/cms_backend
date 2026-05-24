package com.senglorn.cms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    // OpenAPI configuration for the CMS API
    @Bean
    public OpenAPI cmsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CMS API")
                        .description("REST API for the Vue admin frontend")
                        .version("1.0"));
    }
}
