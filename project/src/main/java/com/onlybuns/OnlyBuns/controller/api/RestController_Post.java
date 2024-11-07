package com.onlybuns.OnlyBuns.controller.api;
import com.onlybuns.OnlyBuns.dto.DTO_CreatePost;
import com.onlybuns.OnlyBuns.dto.DTO_View_Post;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.service.Service_Post;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class RestController_Post {

    @Autowired
    private Service_Post servicePost;

    @GetMapping(value = "/api/posts") // /api/posts?sort=newest
    public ResponseEntity<List<DTO_View_Post>> api_posts(@RequestParam(value = "sort", required = false) String sort) { return servicePost.api_posts(sort); }

    @GetMapping("/api/posts/{id}")
    public ResponseEntity<DTO_View_Post> api_posts_id(@PathVariable(name = "id") Integer id) {
        return servicePost.api_posts_id(id);
    }
    @PostMapping("/api/createpost")
    public ResponseEntity<String> api_createpost(@RequestParam("title") String title,
                                                 @RequestParam("description") String description,
                                                 @RequestParam("location") String location,
                                                 @RequestParam(value = "image", required = false) MultipartFile imageFile,
                                                 HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");

        // Validacija podataka
        if (title == null || description == null || location == null) {
            return new ResponseEntity<>("All fields are required.", HttpStatus.BAD_REQUEST);
        }

        // Pozivanje servisne funkcije za kreiranje posta
        return servicePost.api_createpost(title, description, location, imageFile, sessionAccount);
    }

    @GetMapping("/api/posts/{id}/replies")
    public ResponseEntity<List<DTO_View_Post>> api_posts_id_replies(@PathVariable(name = "id") Integer id) {
        return servicePost.api_posts_id_replies(id);
    }

    // TODO: CREATE POST

    // TODO: CREATE POST - PICTURE

    // TODO: CREATE LIKE

    // TODO: POSTS PAGING

    // TODO: UPDATE POST

    // TODO: DELETE POST

}
