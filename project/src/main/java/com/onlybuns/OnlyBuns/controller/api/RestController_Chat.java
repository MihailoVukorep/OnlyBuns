package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Chat;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Message;
import com.onlybuns.OnlyBuns.service.Service_Chat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RestController_Chat {

    @Autowired
    private Service_Chat service_chat;


    @Operation(
            summary = "Current user's chats",
            description = "Returns a list of chats that the currently logged-in user is a member of.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of chats retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DTO_Get_Chat.class))
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user must be logged in")
            }
    )
    @GetMapping("/api/chats")
    public ResponseEntity<List<DTO_Get_Chat>> get_api_chats(HttpSession session) {
        return service_chat.get_api_chats(session);
    }

    // create chat
    @Operation(
            summary = "Create chat",
            description = "Creates a chat between the logged-in user and the account identified by the given ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Chat created successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user must be logged in"),
                    @ApiResponse(responseCode = "404", description = "Account not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/api/accounts/{id}/chat")
    public ResponseEntity<String> get_api_accounts_id_chat(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_chat.get_api_accounts_id_chat(session, id);
    }

    // get chat info
    @Operation(
            summary = "Get chat info by ID",
            description = "Retrieve chat details by its ID for the currently logged-in user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Chat information retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DTO_Get_Chat.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user must be logged in"),
                    @ApiResponse(responseCode = "404", description = "Chat not found or user not a member of the chat")
            }
    )
    @GetMapping("/api/chats/{id}")
    public ResponseEntity<DTO_Get_Chat> get_api_chats_id(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_chat.get_api_chats_id(session, id);
    }

    // get messages
    @Operation(
            summary = "Get chat's messages",
            description = "Retrieve all messages from a chat by its ID for the currently logged-in user. User must be a member of the chat.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of messages retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DTO_Get_Message.class, type = "array"))
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user must be logged in"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user is not a member of the chat"),
                    @ApiResponse(responseCode = "404", description = "Chat not found")
            }
    )
    @GetMapping("/api/chats/{id}/messages")
    public ResponseEntity<List<DTO_Get_Message>> get_api_chats_id_messages(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_chat.get_api_chats_id_messages(session, id);
    }

    // send message to chat
    @Operation(
            summary = "Send message to chat",
            description = "Send a text message to a chat specified by its ID. User must be a member of the chat.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Message sent successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DTO_Get_Message.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user must be logged in"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user is not a member of the chat"),
                    @ApiResponse(responseCode = "404", description = "Chat not found")
            }
    )
    @PostMapping("/api/chats/{id}/messages")
    public ResponseEntity<DTO_Get_Message> post_api_chats_id_messages(HttpSession session, @PathVariable(name = "id") Long id, @RequestParam(required = true) String text) {
        return service_chat.post_api_chats_id_messages(session, id, text);
    }

    // add person to chat
    @Operation(
            summary = "Add person to chat",
            description = "Adds an account (user) to the specified chat. Only the chat admin can perform this action.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Account added to chat successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user is not the admin of the chat"),
                    @ApiResponse(responseCode = "404", description = "Chat or account not found"),
                    @ApiResponse(responseCode = "409", description = "Account already added to chat")
            }
    )
    @GetMapping("/api/chats/{id}/add/{account_id}")
    public ResponseEntity<String> get_api_chats_id_add_id(HttpSession session, @PathVariable(name = "id") Long id, @PathVariable(name = "account_id") Long account_id) {
        return service_chat.get_api_chats_id_add_id(session, id, account_id);
    }

    // remove person from chat
    @Operation(
            summary = "Remove person from chat",
            description = "Removes an account (user) from the specified chat. Only the chat admin can perform this action.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Account removed from chat successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user is not the admin of the chat"),
                    @ApiResponse(responseCode = "404", description = "Chat, account, or chat member not found")
            }
    )
    @GetMapping("/api/chats/{id}/remove/{account_id}")
    public ResponseEntity<String> get_api_chats_id_remove_id(HttpSession session, @PathVariable(name = "id") Long id, @PathVariable(name = "account_id") Long account_id) {
        return service_chat.get_api_chats_id_remove_id(session, id, account_id);
    }

    @Operation(
            summary = "Leave chat",
            description = "Allows the currently logged-in user to leave the chat specified by the chat ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully left the chat"),
                    @ApiResponse(responseCode = "401", description = "User not logged in"),
                    @ApiResponse(responseCode = "404", description = "Chat or member not found")
            }
    )
    @GetMapping("/api/chats/{id}/leave")
    public ResponseEntity<String> get_api_chats_id_leave(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_chat.get_api_chats_id_leave(session, id);
    }
}
