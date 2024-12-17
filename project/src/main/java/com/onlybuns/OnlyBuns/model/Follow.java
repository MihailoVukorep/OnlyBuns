package com.onlybuns.OnlyBuns.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "follow", uniqueConstraints = @UniqueConstraint(columnNames = {"follower", "followee"}))

public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "follower", nullable = false)
    private Account follower;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "followee", nullable = false)
    private Account followee;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;


    public Follow(Account follower, Account followee, LocalDateTime followDateTime) {
        this.follower = follower;
        this.followee = followee;
        this.createdDate = followDateTime;
    }

    @Override
    public String toString() {
        return "Follow{" +
                "id=" + id +
                ", by_account=" + follower.getId() +
                ", account=" + followee.getId() +
                '}';
    }
}
