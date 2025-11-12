package com.sleepy.onlinebankingsystem.security;

import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class JwtUtil {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final long EXPIRATION_MINUTES = 15;

    public String generateToken(String username, Set<UserRole> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(
                        LocalDateTime.now()
                                .plusMinutes(EXPIRATION_MINUTES)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()))
                .signWith(SECRET_KEY)
                .compact();
    }

    // نسخه جدید: استفاده از JwtParserBuilder
    public JwtParser getParser() {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build();
    }

    public Claims validateToken(String token) throws JwtException {
        return getParser()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public String getUsername(Claims claims) {
        return claims.getSubject();
    }

    public Set<String> getRoles(Claims claims) {
        return (Set<String>) claims.get("roles");
    }
}