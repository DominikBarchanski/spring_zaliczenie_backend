package com.example.zaliczenie_spring;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Users, Long> {
    Users findByEmailAndPassword(String email, String password);

    Users findByEmail(String username);
}
