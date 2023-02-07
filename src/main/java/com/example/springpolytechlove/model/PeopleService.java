package com.example.springpolytechlove.model;

import java.util.List;

public interface PeopleService {
    People findByIdAccount(long idAccount);

    List<People> findAllByNameCityAndAgeBetweenAndIdAccountNot(String nameCity, int age, int age2, long idAccount);

    List<People> findAllByNameCityAndGenderAndAgeBetweenAndIdAccountNot(String nameCity, String gender, int age, int age2, long idAccount);

    void saved(People people);
}