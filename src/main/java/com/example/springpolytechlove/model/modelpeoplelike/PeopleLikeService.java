package com.example.springpolytechlove.model.modelpeoplelike;

import com.example.springpolytechlove.model.People;

import java.util.List;

public interface PeopleLikeService {
    List<PeopleLike> findByMainPeople(long id);

    void save(PeopleLike peopleLike);

    void removeByMainPeopleAndLike(long id,long idTwo);
}