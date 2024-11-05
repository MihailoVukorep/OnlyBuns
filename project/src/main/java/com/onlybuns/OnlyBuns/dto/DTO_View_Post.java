package com.onlybuns.OnlyBuns.dto;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Comment;
import com.onlybuns.OnlyBuns.model.Post;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DTO_View_Post {
    public Integer id;
    public String title;
    public String text;
    public Account account;
    public List<Comment> comments;
    public LocalDateTime createdDate;
    public LocalDateTime updatedDate;

    public DTO_View_Post(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.text = post.getText();
        this.account = post.getAccount();
        this.comments = post.getComments();
        this.createdDate = post.getCreatedDate();
        this.updatedDate = post.getUpdatedDate();
    }
}
