package com.jr.todo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.jr.todo.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
    return config.getAuthenticationManager();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailService());
    authenticationProvider.setPasswordEncoder(passwordEncoder);
    return authenticationProvider();
  }

  @Bean
  private UserDetailsService userDetailService() {
    return username -> userRepository.findByEmail(username)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
  }

}
