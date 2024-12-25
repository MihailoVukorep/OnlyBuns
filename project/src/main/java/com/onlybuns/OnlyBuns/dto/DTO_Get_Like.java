package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Like;
import com.onlybuns.OnlyBuns.model.Post;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DTO_Get_Like {
    public Long id;
    public Account account;
    public Post post;

    public DTO_Get_Like(Like like) {
        this.id = like.getId();
        this.account = like.getAccount();
        this.post = like.getPost();
    }
}
