package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_View_Like;
import com.onlybuns.OnlyBuns.dto.DTO_View_Post;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Like;
import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_Like;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
    private Repository_Account repository_account;

    @Autowired
    private Repository_Post repository_post;

    @Autowired
    private Repository_Like repository_like;

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

        List<Post> posts = repository_post.findByParentPostIsNull(sortOrder);
        List<DTO_View_Post> dtos = new ArrayList<>();
        for (Post post : posts) {
            dtos.add(new DTO_View_Post(post));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    public ResponseEntity<DTO_View_Post> api_posts_id(Integer id) {
        Optional<Post> post = repository_post.findById(id);
        if (post.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
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
        repository_post.save(newPost);

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

    public ResponseEntity<List<DTO_View_Post>> api_posts_id_replies(Integer id) {
        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Post post = optional_post.get();
        List<DTO_View_Post> dtos = new ArrayList<>();
        for (Post i : post.getReplies()) { dtos.add(new DTO_View_Post(i)); }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> api_posts_id_like(Integer id, HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) { return new ResponseEntity<>("Can't like when logged out.", HttpStatus.BAD_REQUEST); }

        Optional<Account> optional_account = repository_account.findById(sessionAccount.getId());
        if (optional_account.isEmpty()) { return new ResponseEntity<>("Can't find account", HttpStatus.NOT_FOUND); }
        Account account = optional_account.get();

        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) { return new ResponseEntity<>("Can't find post", HttpStatus.NOT_FOUND); }
        Post post = optional_post.get();

        // create new like
        Like newLike = new Like(account, post);
        post.getLikes().add(newLike);
        repository_like.save(newLike);

        return new ResponseEntity<>("Post liked.", HttpStatus.OK);
    }

    public ResponseEntity<List<DTO_View_Like>> api_posts_id_likes(Integer id) {
        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Post post = optional_post.get();
        List<DTO_View_Like> dtos = new ArrayList<>();
        for (Like i : post.getLikes()) { dtos.add(new DTO_View_Like(i)); }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<String> createReply(Integer postId, Post reply, HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            return new ResponseEntity<>("Can't comment when logged out.", HttpStatus.UNAUTHORIZED);
        }

        Optional<Post> originalPost = repository_post.findById(postId);
        if (originalPost.isEmpty()) {
            return new ResponseEntity<>("Can't find post", HttpStatus.NOT_FOUND);
        }

        Post parentPost = originalPost.get();
        reply.setAccount(sessionAccount);
        reply.setParentPost(parentPost);
        parentPost.getReplies().add(reply);

        repository_post.save(reply);

        return new ResponseEntity<>("Post commented.", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> api_editpost(Integer id,String title, String description, String location,
                                                 MultipartFile imageFile, Account sessionAccount) {

        Optional<Post> existingPostOpt = repository_post.findById(id);
        if (!existingPostOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }

        Post existingPost = existingPostOpt.get();

        String filePath = existingPost.getPicture();

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                filePath = saveImage(imageFile);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
            }
        }

        existingPost.setTitle(title);
        existingPost.setText(description);
        existingPost.setLocation(location);
        existingPost.setPicture(filePath);

        // Save the edited post
        repository_post.save(existingPost);

        System.out.println("Post updated: " + existingPost);
        if (filePath != null) {
            System.out.println("Image path: " + filePath);
        }

        return new ResponseEntity<>("Post updated successfully.", HttpStatus.OK);
    }

    public ResponseEntity<String> api_deletepost(Integer postId, Account sessionAccount) {
        Post post = repository_post.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.status(404).body("Post not found.");
        }

        if (!post.getAccount().getId().equals(sessionAccount.getId())) {
            return ResponseEntity.status(403).body("You do not have permission to delete this post.");
        }

        try {
            repository_post.deleteById(postId);
            return ResponseEntity.status(200).body("Post deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while deleting the post.");
        }
    }
}
