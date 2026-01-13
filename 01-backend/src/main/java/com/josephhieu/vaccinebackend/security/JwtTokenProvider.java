package com.josephhieu.vaccinebackend.security;

import com.josephhieu.vaccinebackend.entity.TaiKhoan;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String JWT_SECRET;

    @Value("${app.jwt.expiration}")
    private long JWT_EXPIRATION;

    public String generateToken(TaiKhoan user) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        // Lấy danh sách quyền đề đưa vào Payload của Token
        Set<String> roles = user.getChiTietPhanQuyens().stream()
                .map(ct -> ct.getPhanQuyen().getTenQuyen())
                .collect(Collectors.toSet());

        return Jwts.builder()
                .setSubject(user.getTenDangNhap())
                .claim("roles", roles) // Lưu quyền để Frontend sử dụng
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromJWT(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(JWT_SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {

        try {
            Jwts.parserBuilder().setSigningKey(JWT_SECRET.getBytes()).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception ex) {
            log.error("JWT validation failed: {}", ex.getMessage());
        }
        return false;
    }
}
