package com.expensemanagement.expense_api.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey ;

    public JwtService(@Value("${jwt.secret}") String secret){
        byte[] keyBytes = Base64.getDecoder().decode(secret) ;

        if(keyBytes.length < 32){
            throw new IllegalArgumentException("JWT_SECRET must contain at least 32 random bytes") ;
        }

        this.secretKey = Keys.hmacShaKeyFor(keyBytes) ;
    }

    public String generateToken(Integer userId,String email){
        Date now = new Date() ;
        Date expiry = new Date(now.getTime() + 15 * 60 * 1000) ;

        return Jwts.builder()
                .subject( String.valueOf(userId))
                .claim("email",email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact() ;
    }

    public Integer extractUserId(String token){
        String subject = Jwts.parser()
                             .verifyWith(secretKey)
                             .build()
                             .parseSignedClaims(token)
                             .getPayload()
                             .getSubject() ;

        return Integer.valueOf(subject) ;
    }
}
