package com.myapp.repository

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = "com.myapp.repository")
@EntityScan("com.myapp.domain")
@EnableAutoConfiguration(exclude = [FlywayAutoConfiguration])
class RepositoryTestConfig {
}
