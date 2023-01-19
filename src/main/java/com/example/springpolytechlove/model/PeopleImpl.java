package com.example.springpolytechlove.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeopleImpl implements PeopleService {
    @Autowired
    private PeopleRepository peopleRepository;

    @Override
    public People findById(long id) {
        return peopleRepository.findById(id);
    }

    @Override
    public List<People> findAllByNameCityAndAgeBetweenAndIdNot(String nameCity, int age, int age2, long id) {
        return peopleRepository.findAllByNameCityAndAgeBetweenAndIdNot(nameCity, age, age2, id);
    }

    @Override
    public List<People> findAllBy() {
        return peopleRepository.findAllBy();
    }

    @Override
    public void save(People people) {
        peopleRepository.save(people);
    }
}