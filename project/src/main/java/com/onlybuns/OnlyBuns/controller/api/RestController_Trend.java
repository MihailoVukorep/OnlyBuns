package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Account;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Post;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Trend;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Trend_Counts;
import com.onlybuns.OnlyBuns.service.Service_Trend;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RestController_Trend {

    @Autowired
    private Service_Trend service_trend;

    @GetMapping("/api/trends")
    public ResponseEntity<DTO_Get_Trend> get_api_trends(HttpSession session) {
        return service_trend.get_api_trends(session);
    }

    @GetMapping("/api/trends/counts")
    public ResponseEntity<DTO_Get_Trend_Counts> get_api_trends_counts(HttpSession session) {
        return service_trend.get_api_trends_counts(session);
    }

    @GetMapping("/api/trends/weekly")
    public ResponseEntity<List<DTO_Get_Post>> get_api_trends_weekly(HttpSession session) {
        return service_trend.get_api_trends_weekly(session);
    }

    @GetMapping("/api/trends/top")
    public ResponseEntity<List<DTO_Get_Post>> get_api_trends_top(HttpSession session) {
        return service_trend.get_api_trends_top(session);
    }

    @GetMapping("/api/trends/likers")
    public ResponseEntity<List<DTO_Get_Account>> get_api_trends_likers(HttpSession session) {
        return service_trend.get_api_trends_likers(session);
    }
}
