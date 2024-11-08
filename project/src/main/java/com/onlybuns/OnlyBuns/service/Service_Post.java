package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_Put_Post;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Like;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Post;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private Repository_Account repository_account;

    @Autowired
    private Repository_Post repository_post;

    @Autowired
    private Repository_Like repository_like;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ResponseEntity<List<DTO_Get_Post>> get_api_posts(String sort) {

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
        List<DTO_Get_Post> dtos = new ArrayList<>();
        for (Post post : posts) {
            dtos.add(new DTO_Get_Post(post));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    public ResponseEntity<DTO_Get_Post> get_api_posts_id(Long id) {
        Optional<Post> post = repository_post.findById(id);
        if (post.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        return new ResponseEntity<>(new DTO_Get_Post(post.get()), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> post_api_createpost(String title, String description, String location,
                                                      MultipartFile imageFile, HttpSession session) {

        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) { return new ResponseEntity<>("Not logged in.", HttpStatus.UNAUTHORIZED); }

        // Validacija podataka
        if (
                title == null ||
                        title.isEmpty() ||
                        description == null ||
                        description.isEmpty() ||
                        location == null ||
                        location.isEmpty())
        {
            return new ResponseEntity<>("All fields are required.", HttpStatus.BAD_REQUEST);
        }
        String filePath = null;  // Inicijalizacija filePath varijable

        try {
            // Save the file to the directory
            filePath = saveImage(imageFile);
            //return ResponseEntity.ok("Image uploaded successfully: " + filePath);
        }
        catch (IOException ignored) {

            // post without image
            //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
        catch (Exception ignored) {

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

    public ResponseEntity<List<DTO_Get_Post>> get_api_posts_id_replies(Long id) {
        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Post post = optional_post.get();
        List<DTO_Get_Post> dtos = new ArrayList<>();
        for (Post i : post.getReplies()) { dtos.add(new DTO_Get_Post(i)); }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> post_api_posts_id_like(Long id, HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) { return new ResponseEntity<>("Can't like when logged out.", HttpStatus.BAD_REQUEST); }

        Optional<Account> optional_account = repository_account.findById(sessionAccount.getId());
        if (optional_account.isEmpty()) { return new ResponseEntity<>("Can't find your account.", HttpStatus.NOT_FOUND); }
        Account account = optional_account.get();

        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) { return new ResponseEntity<>("Can't find post.", HttpStatus.NOT_FOUND); }
        Post post = optional_post.get();

        Optional<Like> optional_like = repository_like.findByAccountIdAndPostId(account.getId(), post.getId());
        if (optional_like.isEmpty()) {

            // create new like
            Like newLike = new Like(account, post);
            post.getLikes().add(newLike);
            repository_like.save(newLike);

            return new ResponseEntity<>("Post liked.", HttpStatus.OK);
        }

        Like like = optional_like.get();
        repository_like.delete(like);

        return new ResponseEntity<>("Post unliked.", HttpStatus.OK);
    }

    public ResponseEntity<List<DTO_Get_Like>> post_api_posts_id_likes(Long id) {
        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Post post = optional_post.get();
        List<DTO_Get_Like> dtos = new ArrayList<>();
        for (Like i : post.getLikes()) { dtos.add(new DTO_Get_Like(i)); }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<String> post_api_posts_id_replies(Long postId, Post reply, HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            return new ResponseEntity<>("Can't comment when logged out.", HttpStatus.UNAUTHORIZED);
        }

        Optional<Post> originalPost = repository_post.findById(postId);
        if (originalPost.isEmpty()) {
            return new ResponseEntity<>("Can't find post.", HttpStatus.NOT_FOUND);
        }

        Post parentPost = originalPost.get();
        reply.setAccount(sessionAccount);
        reply.setParentPost(parentPost);
        parentPost.getReplies().add(reply);

        repository_post.save(reply);

        return new ResponseEntity<>("Post commented.", HttpStatus.OK);
    }

    public ResponseEntity<String> put_api_posts_id(
            @PathVariable(name = "id") Long id,
            DTO_Put_Post dto_put_post,
            HttpSession session) {

        // TODO: UPDATE POST IMPLEMENT
        return new ResponseEntity<>("Not implemented.", HttpStatus.NOT_IMPLEMENTED);

        //return new ResponseEntity<>("Updated post.", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> delete_api_posts_id(
            @PathVariable(name = "id") Long id,
            HttpSession session) {

        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) { return new ResponseEntity<>("Not logged in.", HttpStatus.UNAUTHORIZED); }

        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) { return new ResponseEntity<>("Can't find post.", HttpStatus.NOT_FOUND); }
        Post post = optional_post.get();

        if (!post.getAccount().getId().equals(sessionAccount.getId())) {
            return new ResponseEntity<>("You don't own this post.", HttpStatus.FORBIDDEN);
        }

        repository_post.delete(post);
        return new ResponseEntity<>("Post deleted.", HttpStatus.OK);
    }

}