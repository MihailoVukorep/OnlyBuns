package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Post_Reply;
import com.onlybuns.OnlyBuns.dto.DTO_Put_Post;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Like;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Post;
import com.onlybuns.OnlyBuns.service.Service_Account;
import com.onlybuns.OnlyBuns.service.Service_Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    // posts
    @Operation(summary = "posts")
    @GetMapping(value = "/api/posts")
    public ResponseEntity<Page<DTO_Get_Post>> get_api_posts(
            HttpSession session,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort

    ) {
        return service_post.get_api_posts(session, page, size, sort);
    }

    @Operation(summary = "for you page for current user")
    @GetMapping(value = "/api/fyp")
    public ResponseEntity<Page<DTO_Get_Post>> get_api_fyp(
            HttpSession session,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort

    ) {
        return service_post.get_api_fyp(session, page, size, sort);
    }

    // account's posts
    @Operation(summary = "account's posts")
    @GetMapping("/api/accounts/{id}/posts")
    public ResponseEntity<Page<DTO_Get_Post>> get_api_accounts_id_posts(
            HttpSession session,
            @PathVariable(name = "id") Long id,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        return service_post.get_api_accounts_id_posts(id, session, page, size, sort);
    }

    // get single post
    @Operation(summary = "get single post info")
    @GetMapping("/api/posts/{id}")
    public ResponseEntity<DTO_Get_Post> get_api_posts_id(@PathVariable(name = "id") Long id, HttpSession session) {
        return service_post.get_api_posts_id(id, session);
    }

    // CREATE POST
    @Operation(summary = "create post")
    @PostMapping("/api/posts")
    public ResponseEntity<String> post_api_posts(@RequestParam("title") String title,
                                                 @RequestParam("text") String text,
                                                 @RequestParam("location") String location,
                                                 @RequestParam(value = "image", required = false) MultipartFile imageFile,
                                                 HttpSession session) {
        return service_post.post_api_posts(title, text, location, imageFile, session);
    }

    // GET REPLIES
    @Operation(summary = "post replies")
    @GetMapping("/api/posts/{id}/thread")
    public ResponseEntity<List<DTO_Get_Post>> get_api_posts_id_thread(@PathVariable(name = "id") Long id, HttpSession session) {
        return service_post.get_api_posts_id_thread(id, session);
    }

    // POST REPLY
    @Operation(summary = "reply to post")
    @PostMapping("/api/posts/{id}/reply")
    public ResponseEntity<String> post_api_posts_id_reply(@PathVariable(name = "id") Long id, @RequestBody DTO_Post_Reply replyDTO, HttpSession session) {
        return service_post.post_api_posts_id_reply(id, replyDTO, session);
    }

    // LIKE POST
    @Operation(summary = "like the post")
    @PostMapping("/api/posts/{id}/like")
    public ResponseEntity<String> post_api_posts_id_like(@PathVariable(name = "id") Long id, HttpSession session) {
        return service_post.post_api_posts_id_like(id, session);
    }

    // GET LIKES
    @Operation(summary = "post's likes")
    @GetMapping("/api/posts/{id}/likes")
    public ResponseEntity<List<DTO_Get_Like>> post_api_posts_id_likes(@PathVariable(name = "id") Long id) {
        return service_post.get_api_posts_id_likes(id);
    }

    // UPDATE POST
    @Operation(summary = "update post")
    @PutMapping("/api/posts/{id}")
    public ResponseEntity<String> put_api_posts_id(@PathVariable(name = "id") Long id, DTO_Put_Post dto_put_post, @RequestParam(value = "image", required = false) MultipartFile imageFile, HttpSession session) {
        return service_post.put_api_posts_id(id, dto_put_post, imageFile, session);
    }

    // DELETE POST
    @Operation(summary = "delete post")
    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<String> delete_api_posts_id(@PathVariable(name = "id") Long id, HttpSession session) {
        return service_post.delete_api_posts_id(id, session);
    }

    // TODO: POSTS PAGING
}
