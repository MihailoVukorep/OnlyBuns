package com.onlybuns.OnlyBuns.controller.aspect;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Account;
import com.onlybuns.OnlyBuns.dto.DTO_Get_User;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.service.Service_Account;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Aspect_Main {

    @Autowired
    private Service_Account service_account;

    //@Pointcut("execution(* com.onlybuns.OnlyBuns.controller.web.*.*(..))")
    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controllerMethods() {
    }

    // NOTE: all Controller classes must have Session and Model as args so this runs
    @Before("controllerMethods() && args(session, model, ..)") // Apply to all methods with session and model as args
    public void addAccountToModel(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        // setting the user model for drawing navbar for all pages
        model.addAttribute("user", user == null ? null : new DTO_Get_User(service_account.lazy(user.getId())));
    }
}
