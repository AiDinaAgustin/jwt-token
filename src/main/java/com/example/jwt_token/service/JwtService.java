package com.example.jwt_token.service;


import com.example.jwt_token.dto.TokenPair;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpirationMs;

    // Generate Access Token
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, jwtExpirationMs, new HashMap<>());
    }

    // Generate Refresh Token
    public String generateRefreshToken(Authentication authentication) {
        Map<String, String> claims = new HashMap<>();
        claims.put("tokenType", "refresh");
        return generateToken(authentication, refreshExpirationMs, claims);
    }

    private String generateToken(Authentication authentication, long expirationMs, Map<String, String> claims) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(userPrincipal.getUsername())
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSignInKey())
                .compact();
    }

    // Validate Token
    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        if(!username.equals(userDetails.getUsername())) {
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);

        if(claims != null) {
            return claims.getSubject();
        }
        return null;
    }

    private Claims extractAllClaims(String token) {
        Claims claims = null;

        try {
            claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        return claims;
    }

    // Validate if the token is refresh token
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String tokenType = claims.get("tokenType", String.class);
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            log.error("Error while validating refresh token: {}", e.getMessage());
            return false;
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        return isValidToken(jwt, userDetails);
    }

    public TokenPair generateTokenPair(Authentication authentication) {
        String accessToken = generateAccessToken(authentication);
        String refreshToken = generateRefreshToken(authentication);
        return new TokenPair(accessToken, refreshToken);
    }


}
