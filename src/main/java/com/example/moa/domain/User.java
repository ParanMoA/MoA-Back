package com.example.moa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(length = 50, nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column
    private String gender;

    @Column(length = 15, nullable = false)
    private String name;

    @Column(name = "birth")
    private String birth;

    @Column(name = "matching_count")
    private int matchingCount;

    @Column(name = "favorite_friends")
    private String favoriteFriends;

}