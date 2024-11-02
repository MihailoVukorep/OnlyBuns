package com.onlybuns.OnlyBuns.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "userName", nullable = false, unique = true)
    private String userName;

    @Column(unique = true)
    private String email;

    @Column
    private String address;

    @Column
    private String password;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Temporal(TemporalType.DATE)
    @Column
    private LocalDate dateOfBirth;

    @Column
    private String profilePicture;

    @Column
    private String description;

    @Column
    private AccountRole accountRole;

    public Account(String userName, String email, String address, String password, String firstName, String lastName, LocalDate dateOfBirth, String profilePicture, String description, AccountRole accountRole) {
        this.userName = userName;
        this.email = email;
        this.address = address;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.profilePicture = profilePicture;
        this.description = description;
        this.accountRole = accountRole;
    }
}
