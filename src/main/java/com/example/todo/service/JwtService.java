package com.example.todo.service;

import com.example.todo.model.UserLogin;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;


@Service
public class JwtService {

    String secretKey;

    public JwtService(){
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("HmacSHA256");
            secretKey = Base64.getEncoder().encodeToString(keygen.generateKey().getEncoded());


        } catch (NoSuchAlgorithmException e) {
            System.out.println("Key Generator Failed");
        }
    }

    public String genTokens(String username){
        String jwtTokens = "";

        try {
            jwtTokens = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                    .signWith(SignatureAlgorithm.HS256,getSecretKey())
                    .compact();
        } catch (RuntimeException e) {
            System.out.println("gentoken failed");
        }
        return jwtTokens;
    }

    private String getSecretKey() {
        return secretKey;
    }

    public String getUsername(String tokens) {

        return extractClaims(tokens).getSubject();
    }


    public boolean validateTokens(String tokens, UserDetails userDetails) {

        Claims claims = extractClaims(tokens);

        if(claims == null) {
            return false;
        }

        if(!claims.getSubject().equals(userDetails.getUsername())) {
            return false;
        }
        if(claims.getExpiration().before(new Date())){
            return false;
        }
        return true;
    }


    private Claims extractClaims(String tokens){
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build().parseClaimsJws(tokens).getBody();
    }
}
