package com.myapp.config;

import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    DataSourceProperties dataSourceProperties;

    @SuppressWarnings("ContextJavaBeanUnresolvedMethodsInspection")
    @Bean(destroyMethod = "close")
    @ConfigurationProperties(prefix = DataSourceProperties.PREFIX)
    DataSource realDataSource() {
        return DataSourceBuilder
                .create(this.dataSourceProperties.getClassLoader())
                .url(this.dataSourceProperties.getUrl())
                .username(this.dataSourceProperties.getUsername())
                .password(this.dataSourceProperties.getPassword())
                .build();
    }

    @Bean
    @Primary
    DataSource dataSource() {
        return new DataSourceSpy(realDataSource());
    }

}
