package com.example.springpolytechlove.model.modelpeoplelike;

import com.example.springpolytechlove.model.People;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeopleLikeRepository extends JpaRepository<PeopleLike,Long> {
    List<PeopleLike> findByMainPeople(long id);

    void removeByMainPeople(long id);
}