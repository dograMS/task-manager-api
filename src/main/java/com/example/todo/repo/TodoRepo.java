package com.example.todo.repo;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import com.example.todo.model.Todo;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface TodoRepo extends JpaRepository<Todo, Integer> {

    @Modifying
    @Transactional
    void deleteTodoByUuid(UUID uuid);


    Optional<Todo> findByUuid(UUID uuid);

}
