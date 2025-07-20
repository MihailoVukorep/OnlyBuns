package com.onlybuns.OnlyBuns.controller.web;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Account;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Analytics;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.service.Service_Admin;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Controller
public class Controller_Admin {

    @Autowired
    private Service_Admin service_admin;

    final int PAGE_SIZE = 6;

    @GetMapping("/admin/manage")
    public String admin_manage(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "error/403.html";
        }
        return "admin_manage.html";
    }

    @GetMapping("/admin/accounts")
    public String admin_accounts(HttpSession session,
                                 Model model,
                                 @RequestParam(required = false) String firstName,
                                 @RequestParam(required = false) String lastName,
                                 @RequestParam(required = false) String userName,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) String address,
                                 @RequestParam(required = false) Integer minPostCount,
                                 @RequestParam(required = false) Integer maxPostCount,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(value = "size", required = false, defaultValue = "12") Integer size,
                                 @RequestParam(value = "sort", required = false, defaultValue = "id") String sort
                                 ) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "error/403.html";
        }

        //List<DTO_Get_Account> accounts = service_admin.getFilteredAndSortedAccounts(session, firstName, lastName, userName, email, address, minPostCount, maxPostCount, sort);

        Page<DTO_Get_Account> accountsPage = service_admin.getPaginatedAccounts(
                session, firstName, lastName, userName, email, address,
                minPostCount, maxPostCount, sort, page);

        StringBuilder urlBuilder = new StringBuilder("/admin/accounts");

        List<String> queryParams = new ArrayList<>();
        if (firstName != null && !firstName.isEmpty()) queryParams.add("firstName=" + URLEncoder.encode(firstName, StandardCharsets.UTF_8));
        if (lastName != null && !lastName.isEmpty()) queryParams.add("lastName=" + URLEncoder.encode(lastName, StandardCharsets.UTF_8));
        if (userName != null && !userName.isEmpty()) queryParams.add("userName=" + URLEncoder.encode(userName, StandardCharsets.UTF_8));
        if (email != null && !email.isEmpty()) queryParams.add("email=" + URLEncoder.encode(email, StandardCharsets.UTF_8));
        if (address != null && !address.isEmpty()) queryParams.add("address=" + URLEncoder.encode(address, StandardCharsets.UTF_8));
        if (minPostCount != null) queryParams.add("minPostCount=" + minPostCount);
        if (maxPostCount != null) queryParams.add("maxPostCount=" + maxPostCount);

        queryParams.add("page=" + page);
        queryParams.add("size=" + size);
        queryParams.add("sort=id");

        if (!queryParams.isEmpty()) {
            urlBuilder.append("?").append(String.join("&", queryParams));
        }

        model.addAttribute("baseUrl", urlBuilder.toString());

        model.addAttribute("accounts", accountsPage.getContent());
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", accountsPage.getTotalPages());
        model.addAttribute("totalItems", accountsPage.getTotalElements());
        model.addAttribute("pageSize", size);

        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("userName", userName);
        model.addAttribute("email", email);
        model.addAttribute("address", address);
        model.addAttribute("minPostCount", minPostCount);
        model.addAttribute("maxPostCount", maxPostCount);

        return "admin_accounts.html";
    }

    @GetMapping("/admin/analytics")
    public String admin_analytics(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "error/403.html";
        }

        DTO_Get_Analytics analytics = service_admin.getAnalytics();

        model.addAttribute("analytics", analytics);

        return "admin_analytics.html";
    }
}
