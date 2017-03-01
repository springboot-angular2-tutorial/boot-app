package com.myapp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

import springfox.documentation.builders.PathSelectors.regex

@Configuration
@Profile("dev")
@EnableSwagger2
class Swagger2Config {

    @Bean
    fun swaggerSpringMvcPlugin(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .paths(regex("/api.*"))
            .build()
    }

}
