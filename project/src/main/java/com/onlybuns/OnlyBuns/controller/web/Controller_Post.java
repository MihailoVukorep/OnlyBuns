package com.onlybuns.OnlyBuns.controller.web;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.service.Service_Account;
import com.onlybuns.OnlyBuns.service.Service_Post;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class Controller_Post {

    @Autowired
    private Service_Post service_post;

    @Autowired
    private Service_Account service_account;

    @GetMapping("/posts")
    public String posts(HttpSession session, Model model, @RequestParam(value = "sort", required = false) String sort) {
        model.addAttribute("posts", service_post.get_api_posts_raw(session, sort));
        model.addAttribute("currentSort", sort);
        return "posts.html";
    }

    @GetMapping("/accounts/{id}/posts")
    public String accounts_id_posts(HttpSession session, Model model, @PathVariable(name = "id") Long id, @RequestParam(value = "sort", required = false) String sort) {
        model.addAttribute("posts", service_account.get_api_accounts_id_posts_raw(id, session, sort));
        return "posts.html";
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
