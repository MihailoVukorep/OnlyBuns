package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Comment;
import com.onlybuns.OnlyBuns.model.Like;
import com.onlybuns.OnlyBuns.model.Post;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DTO_View_Like {
    private Long id;
    private Account account;
    private Post post;
    private Comment comment;

    public DTO_View_Like(Like like) {
        this.id = like.getId();
        this.account = like.getAccount();
        this.post = like.getPost();
        this.comment = like.getComment();
    }
}
