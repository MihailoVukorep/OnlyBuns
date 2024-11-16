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
    private Long id;
    private Account account;
    private Post post;

    public DTO_Get_Like(Like like) {
        this.id = like.getId();
        this.account = like.getAccount();
        this.post = like.getPost();
    }
}
