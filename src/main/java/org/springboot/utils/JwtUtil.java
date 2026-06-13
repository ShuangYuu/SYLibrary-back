package org.springboot.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springboot.entity.dto.JwtUser;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtUtil {

    private static final String SECRET_STRING = "mySecret-78373361-@ShuangYu0123456789";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 3600 * 1000;

    public static String createAccessToken(JwtUser jwtUser) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", jwtUser.getId());
        claims.put("username", jwtUser.getUsername());
        claims.put("userImage", jwtUser.getUserImage());
        claims.put("phone", jwtUser.getPhone());
        claims.put("email", jwtUser.getEmail());
        claims.put("role", jwtUser.getRole());

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    public static String createRefreshToken(JwtUser jwtUser) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", jwtUser.getId());
        claims.put("type", "refresh_token");
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("role", jwtUser.getRole());

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
