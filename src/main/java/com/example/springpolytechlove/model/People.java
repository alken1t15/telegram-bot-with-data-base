package com.example.springpolytechlove.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Data
@Entity
@Table(name = "people")
public class People {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_account")
    private Long idAccount;
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

    @Column(name = "account_find")
    private Long accountFind;

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

    @Column(name = "message_like_status")
    private Boolean messageLikeStatus;

    @Column(name = "message_like")
    private String messageLike;

    @Column(name = "img")
    private byte[] img;

    public People(Long idAccount, String name, String nameCity, String gender, String genderFind, String bio, Integer age, String user, Boolean statusInput, Boolean editBio, Boolean statusEditProfile, Boolean statusInstagram, Boolean messageLikeStatus) {
        this.idAccount = idAccount;
        this.name = name;
        this.nameCity = nameCity;
        this.gender = gender;
        this.genderFind = genderFind;
        this.bio = bio;
        this.age = age;
        this.user = user;
        this.statusInput = statusInput;
        this.editBio = editBio;
        this.statusEditProfile = statusEditProfile;
        this.statusInstagram = statusInstagram;
        this.messageLikeStatus = messageLikeStatus;
    }

    public People() {

    }

}