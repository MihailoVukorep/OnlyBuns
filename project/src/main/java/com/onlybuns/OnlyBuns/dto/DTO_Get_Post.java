package com.onlybuns.OnlyBuns.dto;
import com.onlybuns.OnlyBuns.model.Post;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    public LocalDateTime updatedDate;

    public Boolean liked  = false;
    public Boolean myPost = false;

    public DTO_Get_Post(Post post, Boolean liked, Boolean myPost) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.text = post.getText();
        Post parent = post.getParentPost();
        if (parent != null) { this.parentPostId = parent.getId(); }
        this.picture = post.getPicture();
        this.location = post.getLocation();
        this.account = new DTO_Get_Post_Account(post.getAccount());
        this.replies = post.getReplies().size();
        this.likes = post.getLikes().size();
        this.totalChildren = countReplies(post);
        this.createdDate = post.getCreatedDate();
        this.updatedDate = post.getUpdatedDate();
        this.liked = liked;
        this.myPost = myPost;
    }

    private static int countReplies(Post post) {
        int count = 0;
        for (Post reply : post.getReplies()) { count += 1 + countReplies(reply); }
        return count;
    }
}
