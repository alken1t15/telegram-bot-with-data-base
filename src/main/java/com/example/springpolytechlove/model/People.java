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
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "name_city")
    private String nameCity;

    @Column(name = "gender")
    private String gender;

    @Column(name = "gender_find")
    private String genderFind;
    @Column(name = "bio")
    private String bio;
    @Column(name = "age")
    private Integer age;

    @Column(name = "account_inst")
    private String nameInstagram;

    @Column(name = "id_last_account_find")
    private Long idLastAccountFind;

    @Column(name = "user_name")
    private String user;

    @Column(name = "status_input")
    private Boolean statusInput;

    @Column(name = "edit_bio")
    private Boolean editBio;

    @Column(name = "status_edit_profile")
    private Boolean statusEditProfile;

    @Column(name = "status_instagram")
    private Boolean statusInstagram;

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