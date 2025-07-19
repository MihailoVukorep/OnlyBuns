package com.onlybuns.OnlyBuns.dto;

import lombok.*;





@Getter


@Setter


@NoArgsConstructor


@AllArgsConstructor


@ToString


public class DTO_Get_Analytics {
    public int weeklyPostsCount;
    public int weeklyCommentsCount;
    public int monthlyPostsCount;
    public int monthlyCommentsCount;
    public int yearlyPostsCount;
    public int yearlyCommentsCount;
    public double usersPostedPercentage;
    public double usersCommentedPercentage;
    public double noActivityUsersPercentage;

}
