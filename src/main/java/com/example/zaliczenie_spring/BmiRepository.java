package com.example.zaliczenie_spring;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BmiRepository extends CrudRepository<Bmi, Long> {
    List<Bmi> findByUser(Users user);
}