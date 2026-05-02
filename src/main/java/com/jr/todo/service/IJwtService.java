package com.jr.todo.service;

import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.Claims;

public interface IJwtService {

  String getToken(UserDetails user);

  String getUsernameFromToken(String token);

  boolean isTokenValid(String token, UserDetails userDetails);

  <T> T getClaim(String token, Function<Claims, T> claimsResolver);
}
