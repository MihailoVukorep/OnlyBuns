package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Post;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
public class DTO_Get_Post {
    public Long id;
    public String title;
    public String text;
    public String picture;
    public String location;
    public DTO_Get_Post_Account account;
    public Integer replies;
    public Long parentPostId;
    public Integer likes;
    public Integer totalChildren;
    public LocalDateTime createdDate;
    public String createdDateStr;
    public LocalDateTime updatedDate;
    public String updatedDateStr; // TODO: DISPLAY THIS ON FRONTEND
    public Boolean isLiked;
    public Boolean isMyPost;
    public Integer indent;
    public Boolean advertising;

    public DTO_Get_Post(Post post, Boolean isLiked, Boolean isMyPost, Integer indent) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.text = post.getText();
        Post parent = post.getParentPost();
        if (parent != null) {
            this.parentPostId = parent.getId();
        }
        this.picture = post.getPictureUrl();
        this.location = post.getLocation();
        this.account = new DTO_Get_Post_Account(post.getAccount());
        this.replies = post.getReplies().size();
        this.likes = post.getLikes().size();
        this.totalChildren = countReplies(post);
        this.createdDate = post.getCreatedDate();
        this.createdDateStr = this.createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.updatedDate = post.getUpdatedDate();
        this.updatedDateStr = this.updatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.isLiked = isLiked;
        this.isMyPost = isMyPost;
        this.indent = indent;
        this.advertising=post.getAdvertising();
    }

    private static int countReplies(Post post) {
        int count = 0;
        for (Post reply : post.getReplies()) {
            count += 1 + countReplies(reply);
        }
        return count;
    }
}
