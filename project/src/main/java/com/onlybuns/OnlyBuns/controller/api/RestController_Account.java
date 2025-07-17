package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.*;
import com.onlybuns.OnlyBuns.service.Service_Account;
import com.onlybuns.OnlyBuns.service.Service_ScheduleCleanup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class RestController_Account {

    @Autowired
    private Service_Account service_account;

    @Operation(summary = "get current logged in user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "returned user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DTO_Get_Account.class)) }),
            @ApiResponse(responseCode = "404", description = "user not found") })
    @GetMapping("/api/user")
    public ResponseEntity<DTO_Get_Account> get_api_user(HttpSession session) {
        return service_account.get_api_user(session);
    }

    @Operation(summary = "get account by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "returned account",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DTO_Get_Account.class)) }),
            @ApiResponse(responseCode = "404", description = "account not found") })
    @GetMapping("/api/accounts/{id}")
    public ResponseEntity<DTO_Get_Account> get_api_accounts_id(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_account.get_api_accounts_id(session, id);
    }


    @Operation(summary = "get account's likes")
    @ApiResponses(value = {@ApiResponse(responseCode = "404", description = "account not found") })
    @GetMapping("/api/accounts/{id}/likes")
    public ResponseEntity<List<DTO_Get_Like>> get_api_accounts_id_likes(@PathVariable(name = "id") Long id) {
        return service_account.get_api_accounts_id_likes(id);
    }

    @GetMapping("/api/active-users-per-hour")
    public Map<String, Long> getActiveUsersPerHour() {
        return service_account.getActiveUsersLast24hPerHour();
    }

    @Operation(summary = "login endpoint")
    @PostMapping("/api/login")
    public ResponseEntity<String> post_api_login(@RequestBody DTO_Post_AccountLogin dto_post_accountLogin, HttpServletRequest request, HttpSession session) {
        return service_account.post_api_login(dto_post_accountLogin, request, session);
    }

    @Operation(summary = "register endpoint")
    @PostMapping("/api/register")
    public ResponseEntity<String> post_api_register(@RequestBody DTO_Post_AccountRegister dto_post_accountRegister, HttpSession session) {
        return service_account.post_api_register(dto_post_accountRegister, session);
    }

    @Operation(summary = "logout endpoint")
    @PostMapping("/api/logout")
    public ResponseEntity<String> post_api_logout(HttpSession session) {
        return service_account.post_api_logout(session);
    }

    @Operation(summary = "follow this account endpoint")
    @PostMapping("/api/accounts/{id}/follow")
    public ResponseEntity<String> get_api_accounts_id_follow(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_account.post_api_accounts_id_follow(session, id);
    }

    @Operation(summary = "account's followers")
    @GetMapping("/api/accounts/{id}/followers")
    public ResponseEntity<List<DTO_Get_Account>> get_api_accounts_id_followers(@PathVariable(name = "id") Long id) {
        return service_account.get_api_accounts_id_followers(id);
    }

    @Operation(summary = "account's following")
    @GetMapping("/api/accounts/{id}/following")
    public ResponseEntity<List<DTO_Get_Account>> get_api_accounts_id_following(@PathVariable(name = "id") Long id) {
        return service_account.get_api_accounts_id_following(id);
    }
}
