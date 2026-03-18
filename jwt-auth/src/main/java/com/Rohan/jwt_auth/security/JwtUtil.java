package com.Rohan.jwt_auth.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "VGhpcy1pcy1hLXZlcnktc2VjdXJlLWFuZC1sb25nLWJhc2U2NC1zZWNyZXQta2V5LWZvci1qd3Q=";

    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    public String generateToken(String Username){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, Username);
    }

    private String createToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // The username
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // The cryptographic signature
                .compact();

    }

    // --- READ TOKEN ---
    // When a user tries to access a protected API (like deleting a file), they hand this token back to the server. The server needs to read it:
    //
    //parserBuilder().setSigningKey(...): The server uses the SECRET_KEY to unlock the token. Crucially, if a hacker altered even a single letter of the token, this specific method instantly crashes with a SignatureException, protecting your API.
    //
    //Once unlocked, it extracts the Subject (the username) so the server knows exactly who is making the request.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // --- VALIDATE TOKEN ---
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Helper method to convert the Base64 string into a cryptographic Key object
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
