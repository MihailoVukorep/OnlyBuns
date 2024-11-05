package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_View_Post;
import com.onlybuns.OnlyBuns.model.Post;
import org.springframework.data.domain.Sort;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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

        List<Post> posts = repositoryPost.findAll(sortOrder);
        List<DTO_View_Post> dtos = new ArrayList<>();
        for (Post post : posts) { dtos.add(new DTO_View_Post(post)); }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    public ResponseEntity<DTO_View_Post> api_posts_id(@PathVariable(name = "id") Integer id, HttpSession session) {
        Optional<Post> post = repositoryPost.findById(id);
        if (post.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        return new ResponseEntity<>(new DTO_View_Post(post.get()), HttpStatus.OK);
    }
}
