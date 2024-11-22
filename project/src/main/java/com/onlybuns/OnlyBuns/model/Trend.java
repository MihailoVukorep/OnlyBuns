package com.onlybuns.OnlyBuns.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "trendId", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TrendingWeeklyPost> topWeeklyPosts = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "trendId", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TrendingAllTimePost> topAllTimePosts = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "trendId", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TrendingActiveUser> mostActiveLikers = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime lastUpdated;

    public Trend(Long totalPosts, Long postsLastMonth, List<TrendingWeeklyPost> topWeeklyPosts, List<TrendingAllTimePost> topAllTimePosts, List<TrendingActiveUser> mostActiveLikers) {
        this.totalPosts = totalPosts;
        this.postsLastMonth = postsLastMonth;
        this.topWeeklyPosts = topWeeklyPosts;
        this.topAllTimePosts = topAllTimePosts;
        this.mostActiveLikers = mostActiveLikers;
    }

    public Trend(Long totalPosts, Long postsLastMonth) {
        this.totalPosts = totalPosts;
        this.postsLastMonth = postsLastMonth;
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