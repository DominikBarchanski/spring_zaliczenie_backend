package com.example.zaliczenie_spring.repo;

import com.example.zaliczenie_spring.model.Bmi;
import com.example.zaliczenie_spring.model.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BmiRepository extends CrudRepository<Bmi, Long> {
    List<Bmi> findByUser(Users user);
}