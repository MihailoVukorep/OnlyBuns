package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_View_Post;
import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Service_Post {

    @Autowired
    private Repository_Account repositoryAccount;

    @Autowired
    private Repository_Post repositoryPost;

    public ResponseEntity<List<DTO_View_Post>> api_posts() {
        List<Post> posts = repositoryPost.findAll();
        List<DTO_View_Post> dtos = new ArrayList<>();
        for (Post post : posts) { dtos.add(new DTO_View_Post(post)); }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}
