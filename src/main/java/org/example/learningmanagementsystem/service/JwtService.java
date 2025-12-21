package org.example.learningmanagementsystem.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.learningmanagementsystem.entity.User;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final String SECRET_KEY = "qwertyuiopasdfghjklzxcvbnmqwertyuioperthtykjyuksrthqarjuekwtrh";
    private final long EXPIRATION = 1000 * 60 * 60; // 1 soatga beriladi

    public String generateToken(User user) {

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("role", user.getRole().name())
                .claim("username", user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}

