package com.jr.todo.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  private static final String SECRET_KEY = "LSDLSLDED,AOUSD2299-DDEDasde2";

  public String getToke(UserDetails user) {
    return getToken(new HashMap<>(), user);
  }

  private String getToken(Map<String, Object> extraClaims, UserDetails user) {
    return Jwts
        .builder()
        .claims(extraClaims)
        .subject(user.getUsername()).issuedAt(null)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + 1000 * 60))
        .signWith(getKey())
        .compact();
  }

  private Key getKey() {
    byte[] keyByts = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyByts);
  }
}
