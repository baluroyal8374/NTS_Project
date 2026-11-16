package com.nts.hrms.service;

import com.nts.hrms.model.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    private final SecretKey key;

    public JwtService(@Value("${app.jwt-secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String create(AppUser u) {
        return Jwts.builder().subject(u.userCode()).claim("role", u.role())
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 8 * 3_600_000L))
                .signWith(key).compact();
    }

    public Authentication parse(String token) {
        Claims c = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return new UsernamePasswordAuthenticationToken(c.getSubject(), token,
                List.of(new SimpleGrantedAuthority("ROLE_" + c.get("role", String.class))));
    }
}
