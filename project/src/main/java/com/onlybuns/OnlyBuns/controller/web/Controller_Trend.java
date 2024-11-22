package com.onlybuns.OnlyBuns.controller.web;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.service.Service_Trend;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Controller_Trend {

    @Autowired
    private Service_Trend service_trend;

    @GetMapping("/trends")
    public String trends(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }
        return "trends.html";
    }

    @GetMapping("/trends/weekly")
    public String trends_weekly(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }
        model.addAttribute("posts", service_trend.getWeekly(session));
        return "trends_weekly.html";
    }

    @GetMapping("/trends/top")
    public String trends_top(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }
        model.addAttribute("posts", service_trend.getTop(session));
        return "trends_top.html";
    }

    @GetMapping("/trends/likers")
    public String trends_likers(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }
        model.addAttribute("accounts", service_trend.getLikers(session));
        return "trends_likers.html";
    }
}
