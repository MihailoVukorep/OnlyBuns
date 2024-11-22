package com.onlybuns.OnlyBuns.controller.web;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Post;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.service.Service_Account;
import com.onlybuns.OnlyBuns.service.Service_Post;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort
    ) {
        // Use defaults if parameters are null or invalid
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        // Call service to get the posts with pagination
        Page<DTO_Get_Post> postPage = service_post.get_api_posts_raw(session, page, size, sort);

        // Add attributes to the model for Thymeleaf
        model.addAttribute("posts", postPage.getContent()); // Use getContent() for a list of posts
        model.addAttribute("currentSort", sort); // Current sorting criteria
        model.addAttribute("currentPage", page); // Current page number
        model.addAttribute("totalPages", postPage.getTotalPages()); // Total number of pages
        model.addAttribute("pageSize", size); // Page size

        // Return the Thymeleaf template
        return "posts";
    }

    @GetMapping("/accounts/{id}/posts")
    public String accounts_id_posts(
            HttpSession session,
            Model model,
            @PathVariable(name = "id") Long id,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        model.addAttribute("posts", service_post.get_api_accounts_id_posts_raw(id, session, page, size, sort));
        model.addAttribute("currentSort", sort);
        return "posts_raw.html";
    }

    @GetMapping("/user/posts")
    public String user_posts(
            HttpSession session,
            Model model,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }

        model.addAttribute("posts", service_post.get_api_accounts_id_posts_raw(user.getId(), session, page, size, sort));
        model.addAttribute("currentSort", sort);
        return "posts_raw.html";
    }

    @GetMapping("/posts/{id}")
    public String posts_id(HttpSession session, Model model, @PathVariable(name = "id") Long id) {
        model.addAttribute("posts", service_post.get_api_posts_id_thread_raw(id, session)); // draw thread
        return "post.html";
    }

    @GetMapping("/createpost")
    public String createpost(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "error/401.html";
        }
        return "createpost.html";
    }

    @GetMapping("/posts/{id}/edit")
    public String posts_id_edit(HttpSession session, Model model, @PathVariable(name = "id") Long id) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "error/401.html";
        }

        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount == null) {
            return "error/401.html";
        } // Unauthorized
        Optional<Post> optional_post = service_post.findById(id);
        if (optional_post.isEmpty()) {
            return "error/404.html";
        } // Not Found
        Post post = optional_post.get();
        if (!post.getAccount().getId().equals(sessionAccount.getId())) {
            return "error/403.html";
        } // Forbidden -- not your acount

        model.addAttribute("post_id", id);
        return "editpost.html";
    }
}
