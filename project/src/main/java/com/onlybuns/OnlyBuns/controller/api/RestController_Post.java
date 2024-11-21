package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Post_Reply;
import com.onlybuns.OnlyBuns.dto.DTO_Put_Post;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Like;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Post;
import com.onlybuns.OnlyBuns.service.Service_Account;
import com.onlybuns.OnlyBuns.service.Service_Post;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class RestController_Post {

    @Autowired
    private Service_Post service_post;

    @Autowired
    private Service_Account service_account;

    // GET POSTS
    @GetMapping(value = "/api/posts") // /api/posts?sort=newest
    public ResponseEntity<List<DTO_Get_Post>> get_api_posts(HttpSession session, @RequestParam(value = "sort", required = false) String sort) {
        return service_post.get_api_posts(session, sort);
    }

    // GET POSTS FOR ACCOUNT
    @GetMapping("/api/accounts/{id}/posts")
    public ResponseEntity<List<DTO_Get_Post>> get_api_accounts_id_posts(@PathVariable(name = "id") Long id, HttpSession session, @RequestParam(value = "sort", required = false) String sort) {
        return service_account.get_api_accounts_id_posts(id, session, sort);
    }

    // GET POST
    @GetMapping("/api/posts/{id}")
    public ResponseEntity<DTO_Get_Post> get_api_posts_id(@PathVariable(name = "id") Long id, HttpSession session) {
        return service_post.get_api_posts_id(id, session);
    }

    // CREATE POST
    @PostMapping("/api/posts")
    public ResponseEntity<String> post_api_posts(@RequestParam("title") String title,
                                                 @RequestParam("text") String text,
                                                 @RequestParam("location") String location,
                                                 @RequestParam(value = "image", required = false) MultipartFile imageFile,
                                                 HttpSession session) {
        return service_post.post_api_posts(title, text, location, imageFile, session);
    }

    // GET REPLIES
    @GetMapping("/api/posts/{id}/thread")
    public ResponseEntity<List<DTO_Get_Post>> get_api_posts_id_thread(@PathVariable(name = "id") Long id, HttpSession session) {
        return service_post.get_api_posts_id_thread(id, session);
    }

    // POST REPLY
    @PostMapping("/api/posts/{id}/replies")
    public ResponseEntity<String> post_api_posts_id_replies(@PathVariable(name = "id") Long id, @RequestBody DTO_Post_Reply replyDTO, HttpSession session) {
        return service_post.post_api_posts_id_replies(id, replyDTO, session);
    }

    // LIKE POST
    @PostMapping("/api/posts/{id}/like")
    public ResponseEntity<String> post_api_posts_id_like(@PathVariable(name = "id") Long id, HttpSession session) {
        return service_post.post_api_posts_id_like(id, session);
    }

    // GET LIKES
    @GetMapping("/api/posts/{id}/likes")
    public ResponseEntity<List<DTO_Get_Like>> post_api_posts_id_likes(@PathVariable(name = "id") Long id) {
        return service_post.get_api_posts_id_likes(id);
    }

    // UPDATE POST
    @PutMapping("/api/posts/{id}")
    public ResponseEntity<String> put_api_posts_id(@PathVariable(name = "id") Long id, DTO_Put_Post dto_put_post, @RequestParam(value = "image", required = false) MultipartFile imageFile, HttpSession session) {
        return service_post.put_api_posts_id(id, dto_put_post, imageFile, session);
    }

    // DELETE POST
    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<String> delete_api_posts_id(@PathVariable(name = "id") Long id, HttpSession session) {
        return service_post.delete_api_posts_id(id, session);
    }

    // TODO: POSTS PAGING
}
