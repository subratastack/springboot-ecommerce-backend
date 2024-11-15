package com.example.ecommerce_backend.api.security.config;

import com.example.ecommerce_backend.api.security.filter.JwtRequestFilter;
import com.example.ecommerce_backend.exception.FilterExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public WebSecurityConfig(
            JwtRequestFilter jwtRequestFilter
    ) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(filterExceptionHandler(), JwtRequestFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,  "/product").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/auth/register",
                                "/auth/login",
                                "/auth/forgot",
                                "/auth/reset").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/verify").permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public FilterExceptionHandler filterExceptionHandler() {
        return new FilterExceptionHandler();
    }
}
