package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Account;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Analytics;
import com.onlybuns.OnlyBuns.service.Service_Admin;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RestController_Admin {

    @Autowired
    private Service_Admin service_admin;

    @Operation(summary = "listing of all account on platform")
    @GetMapping("/api/admin/accounts")
    public ResponseEntity<List<DTO_Get_Account>> get_api_admin_accounts(
            HttpSession session,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Integer minPostCount,
            @RequestParam(required = false) Integer maxPostCount,
            @RequestParam(defaultValue = "0") Integer pageNum) {
        return service_admin.get_api_admin_accounts(session, firstName, lastName, userName, email, address, minPostCount, maxPostCount, pageNum);

    }

    @Operation(summary = "listing platform analytics")
    @GetMapping("/api/admin/analytics")
    public ResponseEntity<DTO_Get_Analytics> get_api_analytics(){
        DTO_Get_Analytics analytics = service_admin.getAnalytics();
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }
}
