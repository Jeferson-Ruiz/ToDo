package com.jr.todo.config;

import com.jr.todo.jwt.JwtAutenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAutenticationFilter jwtAutenticationFilter;
  private final AuthenticationProvider authProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    return http
        .csrf(csrf -> csrf
            .disable())

        .authorizeHttpRequests(authRequest -> authRequest
            .requestMatchers("/auth/**").permitAll()
            .anyRequest().authenticated())
        .sessionManagement(sesionManager -> sesionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authProvider)
        .addFilterBefore(jwtAutenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}