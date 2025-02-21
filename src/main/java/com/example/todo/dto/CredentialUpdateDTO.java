package com.example.todo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialUpdateDTO {
    private String username;
    private String oldPassoword;
    private String newPassword;
}
