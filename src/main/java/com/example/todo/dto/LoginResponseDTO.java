package com.example.todo.dto;

import com.example.todo.model.UserLogin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private UserLogin user;
    private String tokens;
}
