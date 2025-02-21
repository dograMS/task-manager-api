package com.example.todo.service;

import com.example.todo.model.Todo;
import com.example.todo.model.UserLogin;
import com.example.todo.repo.TodoRepo;
import com.example.todo.repo.UserLoginRepo;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TodosService {

    @Autowired
    private TodoRepo repo;

    @Autowired
    private UserLoginRepo userRepo;


    public UUID addTodo(String username, Todo todo) throws CredentialNotFoundException {
        UserLogin user = userRepo.findByUname(username);
        if(user == null)
            throw new CredentialNotFoundException("user not found adding new todo");

        todo.setUser(user);
        return repo.save(todo).getUuid();

    }

    public UserLogin addTodo(String username,
                        String description,
                        String status,
                        String title,
                        LocalDateTime createDateTime,
                        LocalDateTime dueDateTime) throws CredentialNotFoundException {

        UserLogin user = userRepo.findByUname(username);
        if(user == null)
            throw new CredentialNotFoundException("user not found adding new todo");


        Todo todo = new Todo();
        todo.setDescription(description);
        todo.setStatus(status);
        todo.setTitle(title);
        todo.setCreateDateTime(createDateTime);
        todo.setDueDateTime(dueDateTime);
        todo.setUser(user);

        repo.save(todo);

        return userRepo.findByUname(username);

    }


    public void deleteTodo(UUID user_uuid, UUID uuid)throws Exception{

        Todo task = repo.findByUuid(uuid).get();
        if(task == null && task.getUser().getTodoList().contains(uuid)){
            throw new Exception("Task Not Found");
        }

        repo.deleteTodoByUuid(uuid);


    }


}
