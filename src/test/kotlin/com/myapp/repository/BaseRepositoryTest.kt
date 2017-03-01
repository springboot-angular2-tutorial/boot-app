package com.myapp.repository

import org.junit.runner.RunWith
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.BootstrapWith
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import javax.sql.DataSource
import javax.validation.Validator

@ActiveProfiles("test")
@RunWith(SpringRunner::class)
@BootstrapWith(SpringBootTestContextBootstrapper::class)
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = arrayOf("com.myapp.repository"))
@ImportAutoConfiguration(FlywayAutoConfiguration::class, JooqAutoConfiguration::class)
abstract class BaseRepositoryTest {

    @Configuration
    class RepositoryTestConfig {
        @Bean
        fun validator(): Validator {
            return LocalValidatorFactoryBean()
        }

        @Bean
        @Suppress("SpringKotlinAutowiring")
        fun transactionManager(dataSource: DataSource): PlatformTransactionManager {
            return DataSourceTransactionManager(dataSource)
        }
    }

}