package com.onlybuns.OnlyBuns.controller.web;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class Controller_Map {
    @GetMapping("/map")
    public String viewMap() {
        return "map";
    }
}