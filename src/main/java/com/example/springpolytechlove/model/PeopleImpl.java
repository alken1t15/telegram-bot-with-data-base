package com.example.springpolytechlove.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeopleImpl implements PeopleService {
    @Autowired
    private PeopleRepository peopleRepository;

    @Override
    public List<People> findById(long id) {
        return peopleRepository.findById(id);
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
