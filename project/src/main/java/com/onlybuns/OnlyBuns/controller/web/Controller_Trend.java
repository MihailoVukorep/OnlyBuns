package com.onlybuns.OnlyBuns.controller.web;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Account;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Post;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Trend;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Trend_Counts;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.service.Service_Trend;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping("/trends/counts")
    public String trends_counts(HttpSession session, Model model) {
        ResponseEntity<DTO_Get_Trend_Counts> response = service_trend.get_api_trends_counts(session);
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) { return "error/401.html"; }
        DTO_Get_Trend_Counts trend = response.getBody();
        model.addAttribute("totalPosts", trend.getTotalPosts());
        model.addAttribute("postsLastMonth", trend.getPostsLastMonth());
        model.addAttribute("lastUpdatedStr", trend.getLastUpdatedStr());
        return "trends_counts.html";
    }

    @GetMapping("/trends/weekly")
    public String trends_weekly(HttpSession session, Model model) {
        ResponseEntity<List<DTO_Get_Post>> response = service_trend.get_api_trends_weekly(session);
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) { return "error/401.html"; }
        model.addAttribute("posts", response.getBody());
        return "trends_weekly.html";
    }

    @GetMapping("/trends/top")
    public String trends_top(HttpSession session, Model model) {
        ResponseEntity<List<DTO_Get_Post>> response = service_trend.get_api_trends_top(session);
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) { return "error/401.html"; }
        model.addAttribute("posts", response.getBody());
        return "trends_top.html";
    }

    @GetMapping("/trends/likers")
    public String trends_likers(HttpSession session, Model model) {
        ResponseEntity<List<DTO_Get_Account>> response = service_trend.get_api_trends_likers(session);
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) { return "error/401.html"; }
        model.addAttribute("accounts", response.getBody());
        return "trends_likers.html";
    }
}
