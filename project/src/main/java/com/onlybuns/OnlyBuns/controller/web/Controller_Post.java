package com.onlybuns.OnlyBuns.controller.web;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Post;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.service.Service_Account;
import com.onlybuns.OnlyBuns.service.Service_Post;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class Controller_Post {

    @Autowired
    private Service_Post service_post;

    @GetMapping("/posts")
    public String posts(
            HttpSession session,
            Model model,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "12") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort
    ) {
        Page<DTO_Get_Post> postPage = service_post.get_api_posts(session, page, size, sort).getBody();
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("baseUrl", "/posts");
        return "posts";
    }

    @GetMapping("/fyp")
    public String fyp(
            HttpSession session,
            Model model,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "12") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort
    ) {
        ResponseEntity<Page<DTO_Get_Post>> response = service_post.get_api_fyp(session, page, size, sort);
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) { return "error/401.html"; }
        Page<DTO_Get_Post> postPage = response.getBody();
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("baseUrl", "/fyp");
        return "fyp";
    }

    @GetMapping("/accounts/{id}/posts")
    public String accounts_id_posts(
            HttpSession session,
            Model model,
            @PathVariable(name = "id") Long id,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "12") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort
    ) {
        Page<DTO_Get_Post> postPage = service_post.get_api_accounts_id_posts(id, session, page, size, sort).getBody();
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("baseUrl", "/accounts/" + id + "/posts");
        return "posts_raw.html";
    }

    @GetMapping("/user/posts")
    public String user_posts(
            HttpSession session,
            Model model,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "12") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort
    ) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }
        return accounts_id_posts(session, model, user.getId(), page, size, sort);
    }

    @GetMapping("/posts/{id}")
    public String posts_id(HttpSession session, Model model, @PathVariable(name = "id") Long id) {
        ResponseEntity<List<DTO_Get_Post>> response = service_post.get_api_posts_id_thread(id, session);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) { return "errors/404.html"; }
        model.addAttribute("posts", response.getBody()); // draw thread
        return "post.html";
    }

    @GetMapping("/create")
    public String create(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }
        return "create.html";
    }

    @GetMapping("/posts/{id}/edit")
    public String posts_id_edit(HttpSession session, Model model, @PathVariable(name = "id") Long id) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; } // Unauthorized
        Optional<Post> optional_post = service_post.findById(id);
        if (optional_post.isEmpty()) { return "error/404.html"; } // Not Found
        Post post = optional_post.get();
        if (!post.getAccount().getId().equals(user.getId())) { return "error/403.html"; } // Forbidden -- not your account's post

        model.addAttribute("post_id", id);
        return "edit.html";
    }
}
