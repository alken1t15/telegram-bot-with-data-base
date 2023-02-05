package com.example.springpolytechlove.model.modelpeoplelike;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "people_like")
public class PeopleLike {

    @Id
    @Column(name = "id_main_profile")
    private Long me;

    @Column(name = "id_people_for_like")
    private Long you;

    @Column(name = "message")
    private String message;

    public PeopleLike(Long idILike, Long idYouLike) {
        this.me = idILike;
        this.you = idYouLike;
    }

    public PeopleLike(Long idILike, Long idYouLike,String message) {
        this.me = idILike;
        this.you = idYouLike;
        this.message = message;
    }

    public PeopleLike() {

    }
}