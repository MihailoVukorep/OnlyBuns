package com.onlybuns.OnlyBuns.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trends")
public class Trend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long totalPosts;

    @Column(nullable = false)
    private Long postsLastMonth;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "trending_weekly_posts",
            joinColumns = @JoinColumn(name = "trend_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private List<Post> topWeeklyPosts; // Top 5 most liked posts in last 7 days

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "trending_all_time_posts",
            joinColumns = @JoinColumn(name = "trend_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private List<Post> topAllTimePosts; // Top 10 most liked posts all time

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "trending_active_users",
            joinColumns = @JoinColumn(name = "trend_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private List<Account> mostActiveLikers; // Top 10 users who gave most likes in last 7 days

    @CreationTimestamp
    private LocalDateTime lastUpdated;

    public Trend(Long totalPosts, Long postsLastMonth, List<Post> topWeeklyPosts, List<Post> topAllTimePosts, List<Account> mostActiveLikers) {
        this.totalPosts = totalPosts;
        this.postsLastMonth = postsLastMonth;
        this.topWeeklyPosts = topWeeklyPosts;
        this.topAllTimePosts = topAllTimePosts;
        this.mostActiveLikers = mostActiveLikers;
    }

    @Override
    public String toString() {
        return "Trend{" +
                "id=" + id +
                ", totalPosts=" + totalPosts +
                ", postsLastMonth=" + postsLastMonth +
                ", topWeeklyPosts=" + topWeeklyPosts.size() +
                ", topAllTimePosts=" + topAllTimePosts.size() +
                ", mostActiveLikers=" + mostActiveLikers.size() +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}