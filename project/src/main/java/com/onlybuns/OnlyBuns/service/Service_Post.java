package com.onlybuns.OnlyBuns.service;


import com.onlybuns.OnlyBuns.dto.DTO_CreatePost;
import com.onlybuns.OnlyBuns.dto.DTO_View_Post;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountRole;
import com.onlybuns.OnlyBuns.model.Post;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Service_Post {

    @Autowired
    private Repository_Post repositoryPost;

    public ResponseEntity<List<DTO_View_Post>> api_posts(String sort) {

        // If no sort parameter, use default sorting (e.g., by ID)
        Sort sortOrder = Sort.unsorted();

        if (sort != null && !sort.isEmpty()) {
            // Parse the 'sort' parameter
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String field = sortParams[0];
                String direction = sortParams[1].toUpperCase();

                // Validate direction (ASC or DESC)
                Sort.Direction dir = Sort.Direction.fromString(direction);
                sortOrder = Sort.by(dir, field);
            }
        }

        List<Post> posts = repositoryPost.findByParentPostIsNull(sortOrder);
        List<DTO_View_Post> dtos = new ArrayList<>();
        for (Post post : posts) {
            dtos.add(new DTO_View_Post(post));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    public ResponseEntity<DTO_View_Post> api_posts_id(@PathVariable(name = "id") Integer id) {
        Optional<Post> post = repositoryPost.findById(id);
        if (post.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new DTO_View_Post(post.get()), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> api_createpost(@RequestBody DTO_CreatePost dto_createpost,
                                                 HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");

        String message = dto_createpost.validate();
        if (message != null) {
            return new ResponseEntity<>("All fields are required.", HttpStatus.BAD_REQUEST);
        }


        Post newPost = new Post(
                dto_createpost.getTitle(),
                dto_createpost.getDescription(),
                dto_createpost.getLocation(),
                sessionAccount);
        repositoryPost.save(newPost);

        return new ResponseEntity<>("Post created successfully.", HttpStatus.OK);
    }
    public ResponseEntity<List<DTO_View_Post>> api_posts_id_replies(@PathVariable(name = "id") Integer id) {
        Optional<Post> optional_post = repositoryPost.findById(id);
        if (optional_post.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Post post = optional_post.get();
        List<DTO_View_Post> dtos = new ArrayList<>();
        for (Post i : post.getReplies()) { dtos.add(new DTO_View_Post(i)); }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}
