package com.example.springpolytechlove.model.modelpeoplelike;

import com.example.springpolytechlove.model.People;
import com.example.springpolytechlove.model.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeopleLikeImpl implements PeopleLikeService{

    @Autowired
    private PeopleLikeRepository peopleLikeRepository;

    @Override
    public List<PeopleLike> findByMainPeople(long id) {
        return peopleLikeRepository.findByMainPeople(id);
    }

    @Override
    public void save(PeopleLike peopleLike) {
        peopleLikeRepository.save(peopleLike);
    }

    @Override
    public void removeByMainPeopleAndLike(long id, long idTwo) {
        removeByMainPeopleAndLike(id,idTwo);
    }


}