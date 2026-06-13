package com.arcadiadevs.viora.platform.shared.infrastructure.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cross-origin configuration so a browser-based frontend on a different origin
 * (e.g. the Angular client or the deployed site) can consume the REST API.
 *
 * <p>
 * The API authenticates via explicit identifiers in the request rather than
 * cookies, so credentials are not required and the allowed origins can be left
 * permissive by default. Restrict {@code web.cors.allowed-origins} (env
 * {@code CORS_ALLOWED_ORIGINS}) to the real frontend origins for production.
 * </p>
 */
@Configuration
public class WebCorsConfiguration implements WebMvcConfigurer {

    private final String[] allowedOriginPatterns;

    public WebCorsConfiguration(
            @Value("${web.cors.allowed-origins:*}") String[] allowedOriginPatterns
    ) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOriginPatterns)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
