package com.example.springpolytechlove.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeopleRepository extends JpaRepository<People,Long> {
    List<People> findById(long id);

    List<People> findAllBy();
}