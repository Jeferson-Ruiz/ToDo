package com.jr.todo.modules.auth.jwtAuth;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.jr.todo.modules.auth.service.ITokenBlacklistService;
import com.jr.todo.modules.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final ITokenBlacklistService tokenBlacklistService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    final String token = getTokenFromRequest(request);

    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }

    if (!tokenBlacklistService.isBlackListed(token)) {
      authenticate(request, token);
    }

    filterChain.doFilter(request, response);
  }

  private void authenticate(HttpServletRequest request, String token) {
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      return;
    }

    final String username;
    final String role;
    try {
      username = jwtService.getUsernameFromToken(token);
      role = jwtService.getRoleFromToken(token);
    } catch (Exception e) {
      log.debug("Token no verificable: {}", e.getMessage());
      return;
    }

    if (username == null || role == null) {
      return;
    }

    final UserDetails userDetails;
    try {
      userDetails = userDetailsService.loadUserByUsername(username);
    } catch (Exception e) {
      log.debug("Usuario del token no encontrado: {}", e.getMessage());
      return;
    }

    if (!userDetails.isEnabled() || !jwtService.isTokenValid(token, userDetails)) {
      return;
    }

    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        userDetails.getUsername(),
        null,
        userDetails.getAuthorities());

    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

  private String getTokenFromRequest(HttpServletRequest request) {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }

}