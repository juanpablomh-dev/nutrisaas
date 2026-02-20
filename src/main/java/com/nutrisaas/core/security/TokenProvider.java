package com.nutrisaas.core.security;

import com.nutrisaas.core.model.User;
import com.nutrisaas.core.model.UserTenant;
import com.nutrisaas.core.service.TenantService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class TokenProvider {

    private final Key key;
    private final long validityInMilliseconds;
    private final long longValidityInMilliseconds;
    private final TenantService tenantService;

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public TokenProvider(@Value("${app.jwt.secret}") String secret,
                         @Value("${app.jwt.expiration-ms}") long validityInMilliseconds,
                         @Value("${app.jwt.expiration-ms-long}") long longValidityInMilliseconds, TenantService tenantService) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.validityInMilliseconds = validityInMilliseconds;
        this.longValidityInMilliseconds = longValidityInMilliseconds;
        this.tenantService = tenantService;
    }

    public String createToken(User user, boolean rememberMe, String authorities) {
        Date now = new Date();
        Date exp;
        if (rememberMe) {
            exp = new Date(now.getTime() + longValidityInMilliseconds);
        } else {
            exp = new Date(now.getTime() + validityInMilliseconds);
        }


        UserTenant tenant = tenantService.getDefaultOrFirstTenant(user.getId());
        String tenantDefault = tenant.getTenant().getId();

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("tenant", tenantDefault)
                .claim("roles", authorities)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public Date getIssuedAt(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .getIssuedAt();
    }

    private String getClaimFromToken(String token, String claim) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get(claim, String.class);
    }

    public String getTenantFromToken(String token) {
        return getClaimFromToken(token, "tenant");
    }

}
