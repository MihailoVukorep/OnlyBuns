package com.onlybuns.OnlyBuns.controller.api;
import com.onlybuns.OnlyBuns.dto.DTO_Put_Post;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Like;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Post;
import com.onlybuns.OnlyBuns.model.Post;
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
    private Service_Post servicePost;

    @GetMapping(value = "/api/posts") // /api/posts?sort=newest
    public ResponseEntity<List<DTO_Get_Post>> get_api_posts(@RequestParam(value = "sort", required = false) String sort) { return servicePost.get_api_posts(sort); }

    @GetMapping("/api/posts/{id}")
    public ResponseEntity<DTO_Get_Post> get_api_posts_id(@PathVariable(name = "id") Integer id) {
        return servicePost.get_api_posts_id(id);
    }
    @PostMapping("/api/createpost")
    public ResponseEntity<String> post_api_createpost(@RequestParam("title") String title,
                                                      @RequestParam("description") String description,
                                                      @RequestParam("location") String location,
                                                      @RequestParam(value = "image", required = false) MultipartFile imageFile,
                                                      HttpSession session) {
        return servicePost.post_api_createpost(title, description, location, imageFile, session);
    }

    @GetMapping("/api/posts/{id}/replies")
    public ResponseEntity<List<DTO_Get_Post>> get_api_posts_id_replies(@PathVariable(name = "id") Integer id) {
        return servicePost.get_api_posts_id_replies(id);
    }
    @PostMapping("/api/posts/{id}/replies")
    public ResponseEntity<String> post_api_posts_id_replies(
            @PathVariable(name = "id") Integer id,
            @RequestBody Post reply,
            HttpSession session) {

        return servicePost.post_api_posts_id_replies(id, reply, session);
    }


    @PostMapping("/api/posts/{id}/like")
    public ResponseEntity<String> post_api_posts_id_like(@PathVariable(name = "id") Integer id, HttpSession session) {
        return servicePost.post_api_posts_id_like(id, session);
    }

    @PostMapping("/api/posts/{id}/likes")
    public ResponseEntity<List<DTO_Get_Like>> post_api_posts_id_likes(@PathVariable(name = "id") Integer id) {
        return servicePost.post_api_posts_id_likes(id);
    }

    // TODO: POSTS PAGING

    // TODO: UPDATE POST
    @PutMapping("/api/posts/{id}")
    public ResponseEntity<String> put_api_posts_id(
            @PathVariable(name = "id") Integer id,
            DTO_Put_Post dto_put_post,
            HttpSession session) {

        return servicePost.put_api_posts_id(id, dto_put_post, session);
    }

    // TODO: DELETE POST
    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<String> delete_api_posts_id(
            @PathVariable(name = "id") Integer id,
            HttpSession session) {

        return servicePost.delete_api_posts_id(id, session);
    }
}
