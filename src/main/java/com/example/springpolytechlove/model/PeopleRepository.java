package com.example.springpolytechlove.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeopleRepository extends JpaRepository<People, Long> {
    People findByIdAccount(long idAccount);

    List<People> findAllByNameCityAndAgeBetweenAndIdAccountNot(String nameCity, int age, int age2, long idAccount);

    List<People> findAllByNameCityAndGenderAndAgeBetweenAndIdAccountNot(String nameCity, String gender, int age, int age2, long idAccount);

    List<People> findAllBy();

}