package com.onlybuns.OnlyBuns.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trends")
@ToString
public class Trend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long totalPosts;

    @Column(nullable = false)
    private Long postsLastMonth;

    @Column(columnDefinition = "TEXT")
    private String topWeeklyPostsCsv;

    @Column(columnDefinition = "TEXT")
    private String topAllTimePostsCsv;

    @Column(columnDefinition = "TEXT")
    private String mostActiveLikersCsv;

    @CreationTimestamp
    private LocalDateTime lastUpdated;

    public Trend(Long totalPosts, Long postsLastMonth, String topWeeklyPostsCsv, String topAllTimePosts, String mostActiveLikers) {
        this.totalPosts = totalPosts;
        this.postsLastMonth = postsLastMonth;
        this.topWeeklyPostsCsv = topWeeklyPostsCsv;
        this.topAllTimePostsCsv = topAllTimePosts;
        this.mostActiveLikersCsv = mostActiveLikers;
    }
}