package com.example.zaliczenie_spring.repo;

import com.example.zaliczenie_spring.model.Users;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Users, Long> {
    Users findByEmailAndPassword(String email, String password);

    Users findByEmail(String username);
}
