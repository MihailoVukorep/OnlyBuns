package com.onlybuns.OnlyBuns.service;


import com.onlybuns.OnlyBuns.dto.DTO_View_Post;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Post;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

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
    @Value("${file.upload-dir}")
    private String uploadDir;

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
    public ResponseEntity<String> api_createpost(String title, String description, String location,
                                                 MultipartFile imageFile, Account sessionAccount) {
        // Validacija podataka
        if (title == null || description == null || location == null) {
            return new ResponseEntity<>("All fields are required.", HttpStatus.BAD_REQUEST);
        }
        String filePath = null;  // Inicijalizacija filePath varijable

        try {
            // Save the file to the directory
             filePath = saveImage(imageFile);
            //return ResponseEntity.ok("Image uploaded successfully: " + filePath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }

        // Kreiranje posta
        Post newPost = new Post(title, description, location, filePath, sessionAccount);
        repositoryPost.save(newPost);

        System.out.println("Post kreiran: " + newPost);
        if (filePath != null) {
            System.out.println("Putanja do slike: " + filePath);
        }

        return new ResponseEntity<>("Post created successfully.", HttpStatus.OK);
    }
    private String saveImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/" + filePath.toString();
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
