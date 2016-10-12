package com.myapp.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static java.util.Arrays.asList;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.setAllowedMethods(asList("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE"));
        config.setAllowedHeaders(asList("x-auth-token", "content-type"));
        config.addExposedHeader("x-auth-token");
        config.setMaxAge(864000L);
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }

}
