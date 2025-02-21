package com.example.todo.controller;

import com.example.todo.model.Todo;
import com.example.todo.model.UserLogin;
import com.example.todo.service.TodosService;
import com.example.todo.service.UserLoginService;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.security.auth.login.CredentialNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/todo")
public class TodosController {

    @Autowired
    private TodosService service;

    @Autowired
    private UserLoginService userService;


    @GetMapping
    public ResponseEntity<?> getAllTodo(@AuthenticationPrincipal UserLogin userPrinciple){

        UserLogin user = userService.loadUserByUsername(userPrinciple.getUsername());

        if(user == null){
            return new ResponseEntity("user not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user.getTodoList(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addTask(@AuthenticationPrincipal UserLogin user, @Validated @RequestBody Todo todo){




        UUID uuid;
        try {
            uuid = service.addTodo(user.getUsername(), todo);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(uuid, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTask(
            @AuthenticationPrincipal UserLogin user,
            @RequestBody  Todo todo){


        if(user == null){
            return new ResponseEntity("Your are not authorized",
                    HttpStatus.UNAUTHORIZED);
        }
        try{
            service.deleteTodo(user.getUuid(), todo.getUuid());
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity<?> updateTask(
            @AuthenticationPrincipal UserLogin user,
            @RequestBody  Todo todo){
        /* TODO */
        return new ResponseEntity(HttpStatus.OK);
    }

}
