package com.onlybuns.OnlyBuns.controller.web;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Account;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.service.Service_Post;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Controller_Post {

    @Autowired
    private Service_Post servicePost;

    @GetMapping("/posts")
    public String posts(HttpSession session, Model model, @RequestParam(value = "sort", required = false) String sort) {
        model.addAttribute("posts", servicePost.get_api_posts_raw(session, sort));
        model.addAttribute("currentSort", sort);
        return "posts.html";
    }

    @GetMapping("/posts/{id}")
    public String accounts_id(HttpSession session, Model model, @PathVariable(name = "id") Long id) {
        model.addAttribute("post_id", id); // TODO: DRAW THREAD
        return "post.html";
    }
    @GetMapping("/createpost")
    public String createpost(HttpSession session, Model model) {
        Account user = (Account)session.getAttribute("user");
        if (user == null) { return "error/401.html"; }
        return "createpost.html";
    }

    // TODO: change to /posts/{id}/edit
    @GetMapping("/posts/edit/{id}")
    public String editpost(HttpSession session, Model model, @PathVariable(name = "id") Long id) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }
        model.addAttribute("post_id", id);
        return "editpost.html";
    }
}
