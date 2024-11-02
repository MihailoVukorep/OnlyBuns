package com.onlybuns.OnlyBuns.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Controller_Index {

    @GetMapping("/")
    public String root() { return "index.html"; }

    @GetMapping("/home")
    public String home() { return "redirect:/"; }

    @GetMapping("/index")
    public String index() { return "redirect:/"; }
}
