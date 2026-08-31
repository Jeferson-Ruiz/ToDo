package com.jr.todo.modules.auth.jwtAuth;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jr.todo.exception.ApiError;
import com.jr.todo.modules.auth.service.ITokenBlacklistService;
import com.jr.todo.modules.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final ITokenBlacklistService tokenBlacklistService;
  private final UserDetailsService userDetailsService;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    final String token = getTokenFromRequest(request);
    final String username;

    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }

    if (tokenBlacklistService.isBlackListed(token)) {
      ApiError.write(request, response, objectMapper, HttpServletResponse.SC_UNAUTHORIZED,
          "Sesión cerrada. Inicia sesión nuevamente.");
      return;
    }

    final String role;
    try {
      username = jwtService.getUsernameFromToken(token);
      role = jwtService.getRoleFromToken(token);
    } catch (Exception e) {
      ApiError.write(request, response, objectMapper, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
      return;
    }

    if (username != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails;
      try {
        userDetails = userDetailsService.loadUserByUsername(username);
      } catch (Exception e) {
        ApiError.write(request, response, objectMapper, HttpServletResponse.SC_UNAUTHORIZED, "Usuario no encontrado");
        return;
      }

      if (!userDetails.isEnabled()) {
        ApiError.write(request, response, objectMapper, HttpServletResponse.SC_UNAUTHORIZED, "Usuario desactivado, valida email");
        return;
      }

      if (jwtService.isTokenValid(token, userDetails)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails.getUsername(),
            null,
            userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    filterChain.doFilter(request, response);
  }

  private String getTokenFromRequest(HttpServletRequest request) {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }

}
