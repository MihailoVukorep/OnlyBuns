package com.onlybuns.OnlyBuns.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = true)
    private Post post;

    @Override
    public String toString() {
        return "Like{" +
                "id=" + id +
                ", account=" + account.getId() +
                ", post=" + post.getId() +
                '}';
    }
}
