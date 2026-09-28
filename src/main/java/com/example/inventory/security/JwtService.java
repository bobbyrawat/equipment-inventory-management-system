package com.example.inventory.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final long expirationMs;

  public JwtService(@Value("${app.jwt.secret}") String secret,
                  @Value("${app.jwt.expiration-ms}") long expirationMs) {

    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

    if (keyBytes.length < 32) {
        throw new IllegalStateException(
                "JWT_SECRET must be at least 32 characters long"
        );
    }

    if (expirationMs <= 0) {
        throw new IllegalStateException(
                "JWT expiration must be a positive number"
        );
    }

    this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    this.expirationMs = expirationMs;
}

   

    public String generateToken(InventoryPrincipal principal) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("role", principal.role().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public boolean isValid(String token, String expectedUsername) {
        String username = extractUsername(token);
        return username.equalsIgnoreCase(expectedUsername);
    }

    public long getExpirationMs() { return expirationMs; }
}
