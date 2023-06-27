package com.example.zaliczenie_spring.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    public String getUsernameFromToken(String token) {
        System.out.println("tokenusername: " + token);
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        System.out.println("tokenclaims: " + token);
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        System.out.println("getAllClaimsFromToken: " + token);
        String[] parts = token.split("\\.");
        String claimsJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> claimsMap = objectMapper.readValue(claimsJson, new TypeReference<Map<String, Object>>() {});
            return new DefaultClaims(claimsMap);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize token claims", e);
        }
    }


    private Boolean isTokenExpired(String token, Date expiration) {
        final Date tokenExpiration = getExpirationDateFromToken(token);
        return tokenExpiration.before(expiration);
    }
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plusSeconds(JWT_TOKEN_VALIDITY);

        LocalDateTime issuedAtDateTime = LocalDateTime.ofInstant(issuedAt, ZoneOffset.UTC);
        LocalDateTime expirationDateTime = LocalDateTime.ofInstant(expiration, ZoneOffset.UTC);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails, Date expiration) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, expiration));
    }
}
