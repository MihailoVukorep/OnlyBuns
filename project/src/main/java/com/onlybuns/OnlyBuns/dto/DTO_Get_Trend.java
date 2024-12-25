package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Trend;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DTO_Get_Trend {

    DTO_Get_Trend_Counts counts;
    public String topWeeklyPostsCsv;
    public List<DTO_Get_Post> topWeeklyPosts;
    public String topAllTimePostsCsv;
    public List<DTO_Get_Post> topAllTimePosts;
    public String mostActiveLikersCsv;
    public List<DTO_Get_Account> mostActiveLikers;

    public DTO_Get_Trend(Trend trend, List<DTO_Get_Post> weekly, List<DTO_Get_Post> top, List<DTO_Get_Account> likers) {
        this.counts = new DTO_Get_Trend_Counts(trend);
        this.topWeeklyPostsCsv = trend.getTopWeeklyPostsCsv();
        this.topWeeklyPosts = weekly;
        this.topAllTimePostsCsv = trend.getTopAllTimePostsCsv();
        this.topAllTimePosts = top;
        this.mostActiveLikersCsv = trend.getMostActiveLikersCsv();
        this.mostActiveLikers = likers;
    }
}
