package com.example.springpolytechlove.model;

import com.example.springpolytechlove.model.modelpeoplelike.PeopleLike;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "people")
public class People {
    @Id
    @Column(name = "id")
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "name_city")
    private String nameCity;
    @Column(name = "bio")
    private String bio;

    @Column(name = "age")
    private int age;

    @Column(name = "account_inst")
    private String nameInstagram;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_main_profile")
    private List<PeopleLike> peopleLike;

    public People(long id, String name, String nameCity, String bio, int age) {
        this.id = id;
        this.name = name;
        this.nameCity = nameCity;
        this.bio = bio;
        this.age = age;
    }

    public People() {

    }

}