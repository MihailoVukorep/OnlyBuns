package com.onlybuns.OnlyBuns.controller.api;
import com.onlybuns.OnlyBuns.dto.DTO_View_Post;
import com.onlybuns.OnlyBuns.service.Service_Post;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/api/posts/{id}/replies")
    public ResponseEntity<List<DTO_View_Post>> api_posts_id_replies(@PathVariable(name = "id") Integer id) {
        return servicePost.api_posts_id_replies(id);
    }
}
