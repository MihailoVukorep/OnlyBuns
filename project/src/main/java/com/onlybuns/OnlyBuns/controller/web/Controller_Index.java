package com.onlybuns.OnlyBuns.controller.web;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Account;
import com.onlybuns.OnlyBuns.model.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Controller_Index {

    @GetMapping("/")
    public String root(HttpSession session, Model model) {
        return "index.html";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        return "redirect:/";
    }

    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        return "redirect:/";
    }
}
