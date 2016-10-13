package com.myapp.auth;

import com.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final UserService userService;

    private final TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    public SecurityConfig(UserService userService,
                          TokenAuthenticationService tokenAuthenticationService) {
        super(true);
        this.userService = userService;
        this.tokenAuthenticationService = tokenAuthenticationService;
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
                .antMatchers(HttpMethod.GET, "/api/users/me/microposts").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/microposts/**").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/api/microposts/**").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/relationships/**").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/api/relationships/**").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/api/feed").hasRole("USER")
        ;

        http.addFilterBefore(
                new StatelessLoginFilter("/api/login", tokenAuthenticationService, userService, authenticationManager()),
                UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(
                new StatelessAuthenticationFilter(tokenAuthenticationService),
                UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
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


