package com.example.moa.jwt;

import com.example.moa.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtTokenUtil {
    private String secretKey = "tlstnalsdjelTdjskwhffuqorhvkdlrjakwsmsrjdlagkrltlfgekwlsWk";
    private Key key;
    private long tokenValidTime = 30 * 60 * 1000L;
    Date now = new Date();

    //Base64로 인코딩
    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600000);
        Claims claims = Jwts.claims().setSubject(user.getEmail());

        claims.put("key", "value");
        claims.put("email", user.getEmail());

        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .signWith(key, SignatureAlgorithm.HS256)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }

    // JWT 토큰에서 Claims 추출
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmailFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

        return true;
    }

}
