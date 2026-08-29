package com.jr.todo.modules.auth.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService implements IJwtService {

  @Value("${jwt.secret}")
  private String SECRET_KEY;

  @Value("${jwt.expiration}")
  private Long EXPIRATION;

  public String getToken(UserDetails user) {
    Map<String, Object> extraClaims = new HashMap<>();
    if (user instanceof com.jr.todo.modules.user.entity.User u && u.getRole() != null) {
      extraClaims.put("role", u.getRole().name());
      
    } else if (!user.getAuthorities().isEmpty()) {
      String authority = user.getAuthorities().iterator().next().getAuthority();
      extraClaims.put("role", authority.replace("ROLE_", ""));
    }
    return getToken(extraClaims, user);
  }

  private String getToken(Map<String, Object> extraClaims, UserDetails user) {
    return Jwts.builder()
        .claims(extraClaims)
        .id(UUID.randomUUID().toString())
        .subject(user.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
        .signWith(getKey())
        .compact();
  }

  private SecretKey getKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String getUsernameFromToken(String token) {
    return getClaim(token, Claims::getSubject);
  }

  public String getJtiFromToken(String token) {
    return getClaim(token, Claims::getId);
  }

  public String getRoleFromToken(String token) {
    return getClaim(token, claims -> claims.get("role", String.class));
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  private Claims getAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Date getExpiration(String token) {
    return getClaim(token, Claims::getExpiration);
  }

  private boolean isTokenExpired(String token) {
    return getExpiration(token).before(new Date());
  }

}