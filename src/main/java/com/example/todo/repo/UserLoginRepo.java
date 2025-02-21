package com.example.todo.repo;

import com.example.todo.model.Todo;
import jakarta.transaction.Transactional;
import org.hibernate.annotations.OnDelete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.todo.model.UserLogin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserLoginRepo extends JpaRepository<UserLogin, Integer> {

    @Query("SELECT u FROM UserLogin u WHERE u.username = :uname")
    UserLogin findByUname(String uname);

    @Query("SELECT u FROM UserLogin u WHERE " +
            "u.username = :#{#newuser.username} OR " +
            "u.email    = :#{#newuser.email}")
    UserLogin findCredentialMatch(@Param("newuser") UserLogin newuser);


    @Modifying
    @Transactional
    @Query("DELETE FROM UserLogin u WHERE u.uuid = :uuid")
    void deleteUserById(UUID uuid);

//    @Query("SELECT u FORM UserLogin u WHERE uuid = :uuid")
    Optional<UserLogin> findByUuid(UUID uuid);

    Optional<UserLogin> findByEmail(String email);

//    @Query("SELECT u.todoList FROM u WHERE u.uuid = :uuid")
//    List<Todo> getTodoListByUname(UUID uuid);
}
