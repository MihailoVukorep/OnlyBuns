package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Trend;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DTO_Get_Trend_Counts {

    public Long totalPosts;
    public Long postsLastMonth;
    public LocalDateTime lastUpdated;
    public String lastUpdatedStr;

    public DTO_Get_Trend_Counts(Trend trend) {
        this.totalPosts = trend.getTotalPosts();
        this.postsLastMonth = trend.getPostsLastMonth();
        this.lastUpdated = trend.getLastUpdated();
        this.lastUpdatedStr = this.lastUpdated.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
