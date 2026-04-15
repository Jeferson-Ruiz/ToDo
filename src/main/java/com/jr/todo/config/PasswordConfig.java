package com.jr.todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class PasswordConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /*
   * Desactivar seguridad para pruebas
   * 
   * @Configuration
   * 
   * @EnableWebSecurity
   * public class CsrfSecurityConfig {
   * 
   * @Bean
   * public SecurityFilterChain securityFilterChain(HttpSecurity http) {
   * http
   * .csrf((csrf) -> csrf.disable());
   * return http.build();
   * }
   * }
   */
}