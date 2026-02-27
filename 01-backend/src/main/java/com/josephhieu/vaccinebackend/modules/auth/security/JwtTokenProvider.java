package com.josephhieu.vaccinebackend.modules.auth.security;

import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
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

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    public String generateToken(TaiKhoan user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        Set<String> authorities = user.getChiTietPhanQuyens().stream()
                .map(ct -> ct.getPhanQuyen().getTenQuyen())
                .collect(Collectors.toSet());

        return Jwts.builder()
                .setSubject(user.getTenDangNhap())
                .claim("authorities", authorities)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Token không đúng định dạng (Invalid JWT token)");
        } catch (ExpiredJwtException ex) {
            log.error("Token đã hết hạn (Expired JWT token)");
        } catch (UnsupportedJwtException ex) {
            log.error("Token không được hỗ trợ (Unsupported JWT token)");
        } catch (IllegalArgumentException ex) {
            log.error("Chuỗi Claims của JWT trống (JWT claims string is empty)");
        } catch (SignatureException ex) {
            log.error("Chữ ký Token không hợp lệ (Invalid JWT signature)");
        }
        return false;
    }
}
