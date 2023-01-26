package com.example.springpolytechlove.model;

import java.util.List;

public interface PeopleService {
    People findById(long id);

    List<People> findAllByNameCityAndAgeBetweenAndIdNot(String nameCity, int age, int age2, long id);


    void save(People people);
}