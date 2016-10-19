package com.myapp.auth;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityFilterConfig {

    @Bean
    public StatelessAuthenticationFilter statelessAuthenticationFilter(TokenAuthenticationService tokenAuthenticationService) throws Exception {
        return new StatelessAuthenticationFilter(tokenAuthenticationService);
    }

    @Bean
    public FilterRegistrationBean registration(StatelessAuthenticationFilter filter) {
        // http://stackoverflow.com/questions/28421966/prevent-spring-boot-from-registering-a-servlet-filter/28428154#28428154
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }

}
