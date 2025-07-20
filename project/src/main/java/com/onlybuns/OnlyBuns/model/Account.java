package com.onlybuns.OnlyBuns.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column
    private String password;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String address;

    @Column
    private String avatar = "/avatars/default.jpg";

    @Column
    private String bio;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @Column
    private LocalDateTime lastActivityDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "account_roles",
            joinColumns = @JoinColumn(name = "account_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    // One-to-many relationship with Post - an account can have many posts
    @OneToMany(mappedBy = "account", cascade = CascadeType.REFRESH, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToMany(mappedBy = "account", cascade = CascadeType.REFRESH, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Like> likes;

    @Column(name = "follower_count", columnDefinition = "integer default 0")
    private Integer followerCount = 0;

//    public void follow(Account account) {
//        this.following.add(account);
//        account.getFollowers().add(this);
//    }
//
//    public void unfollow(Account account) {
//        this.following.remove(account);
//        account.getFollowers().remove(this);
//    }

    public Account(String email, String userName, String password, String firstName, String lastName, String address, String avatar, String bio) {
        this.email = email;
        this.userName = userName;
        this.password = hashPassword(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        if (!avatar.isEmpty()) this.avatar = avatar;
        this.bio = bio;
    }

    public Account(Long id, String userName, String email, String password) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.password = hashPassword(password);
        this.followerCount = 0;
    }

    public Account(String email, String userName, String password, String firstName, String lastName, String address, String avatar, String bio, Role role) {
        this.email = email;
        this.userName = userName;
        this.password = hashPassword(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        if (!avatar.isEmpty()) this.avatar = avatar;
        this.bio = bio;
        this.roles.add(role);
    }

    public Account(String email, String userName, String password, String firstName, String lastName, String address, String avatar, String bio, Role role, LocalDateTime creation) {
        this.email = email;
        this.userName = userName;
        this.password = hashPassword(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        if (!avatar.isEmpty()) this.avatar = avatar;
        this.bio = bio;
        this.roles.add(role);
        this.createdDate = creation;
    }

    public Account( String email, String userName, String password, String firstName, String lastName, String address, String bio, LocalDateTime createdDate) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.bio = bio;
        this.createdDate = createdDate;
    }

    private String hashPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    public boolean isPassword(String rawPassword) {
        return new BCryptPasswordEncoder().matches(rawPassword, this.password);
    }

    public boolean isAdmin() {
        return this.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", avatar='" + avatar + '\'' +
                ", bio='" + bio + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
