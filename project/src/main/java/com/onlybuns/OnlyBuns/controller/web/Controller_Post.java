package com.onlybuns.OnlyBuns.controller.web;
import com.onlybuns.OnlyBuns.model.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class Controller_Post {

    @GetMapping("/posts")
    public String posts() { return "posts.html"; }

    @GetMapping("/posts/{id}")
    public String accounts_id(@PathVariable(name = "id") Long id, Model model) {
        model.addAttribute("post_id", id);
        return "post.html";
    }
    @GetMapping("/createpost")
    public String createpost(HttpSession session) {
        Account user = (Account)session.getAttribute("account");
        if (user == null) { return "error/401.html"; }
        return "createpost.html";
    }

    @GetMapping("/posts/{id}/edit")
    public String editpost(@PathVariable(name = "id") Long id, HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("account");
        if (user == null) {
            return "error/401.html";
        }

        model.addAttribute("post_id", id);
        return "editpost.html";
    }
}