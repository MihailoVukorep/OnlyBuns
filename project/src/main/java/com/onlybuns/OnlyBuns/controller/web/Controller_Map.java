package com.onlybuns.OnlyBuns.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Controller_Map {

    @GetMapping("/map")
    public String viewMap() {
        // Ovdje se može dodati logika ako je potrebna, npr. učitavanje podataka o lokaciji
        return "map"; // Vraća naziv HTML stranice koja sadrži mapu
    }
}
