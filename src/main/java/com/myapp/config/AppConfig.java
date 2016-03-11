package com.myapp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = AppConfig.PREFIX)
@Data
public class AppConfig {

    public static final String PREFIX = "app";

    private String assetHost;
    private String assetManifestUrl;
}
