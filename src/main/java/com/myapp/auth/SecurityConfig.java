package com.myapp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userService;
    private final StatelessAuthenticationFilter statelessAuthenticationFilter;

    @Autowired
    public SecurityConfig(UserDetailsService userService, StatelessAuthenticationFilter statelessAuthenticationFilter) {
        super(true);
        this.userService = userService;
        this.statelessAuthenticationFilter = statelessAuthenticationFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // we use jwt so that we can disable csrf protection
        http.csrf().disable();

        http
                .exceptionHandling().and()
                .anonymous().and()
                .servletApi().and()
                .headers().cacheControl()
        ;

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/users").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/api/users/me").hasRole("USER")
                .antMatchers(HttpMethod.PATCH, "/api/users/me").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/api/users/me/microposts").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/microposts/**").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/api/microposts/**").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/relationships/**").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/api/relationships/**").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/api/feed").hasRole("USER")
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new Http401AuthenticationEntryPoint("'Bearer token_type=\"JWT\"'"));

        http.addFilterBefore(statelessAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Prevent StatelessAuthenticationFilter will be added to Spring Boot filter chain.
     * Only Spring Security must use it.
     */
    @Bean
    public FilterRegistrationBean registration(StatelessAuthenticationFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return userService;
    }

}


