package com.example.todo.controller;

import com.example.todo.dto.CredentialUpdateDTO;
import com.example.todo.dto.ErrorResponseDTO;
import com.example.todo.dto.LoginResponseDTO;
import com.example.todo.dto.SuccessResponseDTO;
import com.example.todo.model.UserLogin;
import com.example.todo.service.JwtService;
import com.sun.net.httpserver.Authenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.CredentialNotFoundException;

import com.example.todo.service.UserLoginService;

import java.nio.file.attribute.UserPrincipal;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/login")
public class LoginRegister {

    @Autowired
    UserLoginService service;

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationProvider provider;

    @GetMapping
    private String hello(@AuthenticationPrincipal UserLogin user) {

        if(user == null)
            return "you arent authorized";

        return "hello:";
    }

    @PostMapping("/signv1")
    private ResponseEntity<?> login(@RequestBody UserLogin user) {
        System.out.println("Login hit");

        LoginResponseDTO loginResponse = new LoginResponseDTO();

        try {
            String username = "";
            if(!user.getUsername().isEmpty()) {
                username = user.getUsername();
            } else{
                username = service.loadUserByEmail(user.getEmail()).getUsername();
            }

            loginResponse.setUser(service.loginUser(username, user.getPassword()));
            loginResponse.setTokens(jwtService.genTokens(username));


        } catch (CredentialNotFoundException e) {
            return new ResponseEntity<>( new ErrorResponseDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("signv1/logout")
    private ResponseEntity<?> logout(){



        return new ResponseEntity<>(new SuccessResponseDTO("Logout Success"), HttpStatus.OK);
    }

    @PostMapping("/signv1/signup")
    private ResponseEntity<?> registerUser(@RequestBody UserLogin user) {
        System.out.println("register hit");


        try {
            service.addUser(user.getUsername(), user.getEmail(), user.getPassword());
        } catch (Exception e) {
            return new ResponseEntity<>( new ErrorResponseDTO(e.getMessage()), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }




    @DeleteMapping
    private ResponseEntity<?> deleteAccount( @AuthenticationPrincipal UserLogin authUser,
            @RequestBody UserLogin user){
        try {

            if(service.matchCredentials(authUser, user)){
                service.deleteUser(user.getUsername(), user.getPassword());
            }

        } catch (Exception e) {
            return new ResponseEntity<>( new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


    @PostMapping("/update")
    ResponseEntity<?> updateUserDetails(@AuthenticationPrincipal UserLogin authUser,
            @RequestBody UserLogin user){

        String newTokens = "";
        try{
            newTokens = service.updateUserDetails(authUser, user);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new LoginResponseDTO(authUser, newTokens), HttpStatus.OK);
    }

    @PostMapping("/update/password")
    ResponseEntity<?> updateUserCredentials(@AuthenticationPrincipal UserLogin authUser,
                                 @RequestBody CredentialUpdateDTO newCredetials){



        try{
            service.updateUserCreadentials(authUser, newCredetials);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}