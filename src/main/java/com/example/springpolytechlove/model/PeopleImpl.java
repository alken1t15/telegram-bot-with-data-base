package com.example.springpolytechlove.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class PeopleImpl implements PeopleService {
    @Autowired
    private PeopleRepository peopleRepository;

    @Override
    public People findByIdAccount(long idAccount) {
        return peopleRepository.findByIdAccount(idAccount);
    }

    @Override
    public List<People> findAllByNameCityAndAgeBetweenAndIdAccountNot(String nameCity, int age, int age2, long idAccount) {
        return peopleRepository.findAllByNameCityAndAgeBetweenAndIdAccountNot(nameCity, age, age2, idAccount);
    }

    @Override
    public List<People> findAllByNameCityAndGenderAndAgeBetweenAndIdAccountNot(String nameCity, String gender, int age, int age2, long idAccount) {
        return peopleRepository.findAllByNameCityAndGenderAndAgeBetweenAndIdAccountNot(nameCity, gender,age, age2, idAccount);
    }

    @Override
    public void saved(People people) {
        peopleRepository.save(people);
    }

}