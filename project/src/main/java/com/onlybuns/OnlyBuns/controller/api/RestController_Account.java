package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.*;
import com.onlybuns.OnlyBuns.service.Service_Account;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;
import java.util.Map;

@RestController
public class RestController_Account {

    @Autowired
    private Service_Account service_account;

    @Operation(
            summary = "Get currently logged-in user",
            description = "Returns the profile data of the currently authenticated user from the session. Includes flags like `isMyAccount` and `isFollowing`.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Current user profile retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO_Get_Account.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Not logged in (no session user found)"
                    )
            }
    )
    @GetMapping("/api/user")
    public ResponseEntity<DTO_Get_Account> get_api_user(HttpSession session) {
        return service_account.get_api_user(session);
    }


    @Operation(
            summary = "Get public account profile by ID",
            description = "Returns the account's public profile data. If the requester is logged in, includes 'isMyAccount' and 'isFollowing' status.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the account to retrieve",
                            required = true,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Account found and returned successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO_Get_Account.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Account not found")
            }
    )
    @GetMapping("/api/accounts/{id}")
    public ResponseEntity<DTO_Get_Account> get_api_accounts_id(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_account.get_api_accounts_id(session, id);
    }


    @Operation(
            summary = "Get all likes made by a specific account",
            description = "Returns a list of likes (e.g., liked posts or entities) associated with the specified account by ID.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the account whose likes are being retrieved",
                            required = true,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of likes retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DTO_Get_Like.class))
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Account not found")
            }
    )
    @GetMapping("/api/accounts/{id}/likes")
    public ResponseEntity<List<DTO_Get_Like>> get_api_accounts_id_likes(@PathVariable(name = "id") Long id) {
        return service_account.get_api_accounts_id_likes(id);
    }

    @Operation(
            summary = "Get active users count per hour for the last 24 hours",
            description = "Returns a map where keys are hourly time strings (e.g., '14:00') and values are the count of active users during that hour.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Hourly active user counts retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = java.util.Map.class,
                                            example = "{\"10:00\": 15, \"11:00\": 23, \"12:00\": 30}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/api/active-users-per-hour")
    public Map<String, Long> getActiveUsersPerHour() {
        return service_account.getActiveUsersLast24hPerHour();
    }

    @Operation(
            summary = "Log in a user",
            description = "Authenticates a user by email or username and password. Handles session creation, rate limiting, and email verification. Returns appropriate HTTP status based on result.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DTO_Post_AccountLogin.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully logged in"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or already logged in"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - wrong password or email not verified"),
                    @ApiResponse(responseCode = "404", description = "Account not found"),
                    @ApiResponse(responseCode = "429", description = "Too many login attempts"),
                    @ApiResponse(responseCode = "500", description = "Internal server error (e.g., email sending failed)")
            }
    )
    @PostMapping("/api/login")
    public ResponseEntity<String> post_api_login(@RequestBody DTO_Post_AccountLogin dto_post_accountLogin, HttpServletRequest request, HttpSession session) {
        return service_account.post_api_login(dto_post_accountLogin, request, session);
    }

    @Operation(
            summary = "Register a new user account",
            description = "Registers a new account with provided email, username, and personal details. Applies validation, uniqueness checks using a Bloom filter, and sends a verification email.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DTO_Post_AccountRegister.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Registration successful. Verification email sent."),
                    @ApiResponse(responseCode = "400", description = "Invalid input or already logged in."),
                    @ApiResponse(responseCode = "409", description = "Email or username already exists."),
                    @ApiResponse(responseCode = "500", description = "Internal server error (e.g., email sending failure).")
            }
    )
    @PostMapping("/api/register")
    public ResponseEntity<String> post_api_register(@RequestBody DTO_Post_AccountRegister dto_post_accountRegister, HttpSession session) {
        return service_account.post_api_register(dto_post_accountRegister, session);
    }

    @Operation(
            summary = "Log out the current user",
            description = "Invalidates the current session and logs out the user if logged in.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully logged out."),
                    @ApiResponse(responseCode = "400", description = "User was already logged out.")
            }
    )
    @PostMapping("/api/logout")
    public ResponseEntity<String> post_api_logout(HttpSession session) {
        return service_account.post_api_logout(session);
    }


    @Operation(
            summary = "Follow or unfollow an account by ID",
            description = "Toggles follow status: if the current user is not following the target account, it follows; otherwise, it unfollows. Requires login and rate limiting applies.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the account to follow or unfollow",
                            required = true,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Followed or unfollowed successfully"),
                    @ApiResponse(responseCode = "401", description = "User not logged in"),
                    @ApiResponse(responseCode = "403", description = "Follow limit exceeded"),
                    @ApiResponse(responseCode = "404", description = "Follower or followee account not found"),
                    @ApiResponse(responseCode = "409", description = "User cannot follow themselves")
            }
    )
    @PostMapping("/api/accounts/{id}/follow")
    public ResponseEntity<String> get_api_accounts_id_follow(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_account.post_api_accounts_id_follow(session, id);
    }

    @Operation(
            summary = "Get followers of an account",
            description = "Returns a list of accounts that follow the specified account by ID.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the account whose followers are being requested",
                            required = true,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of followers retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DTO_Get_Account.class))
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Account not found")
            }
    )
    @GetMapping("/api/accounts/{id}/followers")
    public ResponseEntity<List<DTO_Get_Account>> get_api_accounts_id_followers(@PathVariable(name = "id") Long id) {
        return service_account.get_api_accounts_id_followers(id);
    }

    @Operation(
            summary = "Get accounts followed by the specified user",
            description = "Returns a list of accounts that the given user (by ID) is following.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the account whose followings are being requested",
                            required = true,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of followed accounts retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DTO_Get_Account.class))
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Account not found")
            }
    )
    @GetMapping("/api/accounts/{id}/following")
    public ResponseEntity<List<DTO_Get_Account>> get_api_accounts_id_following(@PathVariable(name = "id") Long id) {
        return service_account.get_api_accounts_id_following(id);
    }
}
