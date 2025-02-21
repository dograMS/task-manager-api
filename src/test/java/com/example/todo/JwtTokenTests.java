package com.example.todo;

import com.example.todo.Configuration.SecurityConfig;
import com.example.todo.model.UserLogin;
import com.example.todo.service.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtTokenTests {

    @Autowired
    JwtService jwtService;

    @Autowired
    SecurityConfig securityConfig;

    @Test
    void getAndValidateTokens(){

        String jwtTokens = jwtService.genTokens("Mani");

        UserLogin userDetails = new UserLogin();
        userDetails.setUsername("Mani");
        userDetails.setEmail("mani@mail.com");
        userDetails.setPassword(getHashPass("pass"));

        Assertions.assertTrue(jwtService.validateTokens(jwtTokens, userDetails), "Tokken validation failed ");
    }


    String getHashPass(String password){
        return securityConfig.passwordEncoder().encode(password);
    }
}
