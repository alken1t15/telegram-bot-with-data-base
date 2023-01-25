package com.example.springpolytechlove.model.modelpeoplelike;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeopleLikeRepository extends JpaRepository<PeopleLike,Long> {
    List<PeopleLike> findByYou(long id);

}