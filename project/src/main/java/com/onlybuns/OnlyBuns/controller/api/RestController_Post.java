package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Post_Reply;
import com.onlybuns.OnlyBuns.dto.DTO_Put_Post;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Like;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Post;
import com.onlybuns.OnlyBuns.service.Service_Account;
import com.onlybuns.OnlyBuns.service.Service_Post;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;

@RestController
public class RestController_Post {

    @Autowired
    private Service_Post service_post;

    @Autowired
    private Service_Account service_account;

    // posts
    @Operation(
            summary = "Retrieve paginated posts",
            description = "Returns a paginated list of posts (only root posts, no replies), optionally sorted.",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)", required = false),
                    @Parameter(name = "size", description = "Page size", required = false),
                    @Parameter(name = "sort", description = "Sort order, e.g. 'createdDate,desc'", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Page of posts retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user session invalid or missing")
            }
    )
    @GetMapping(value = "/api/posts")
    public ResponseEntity<Page<DTO_Get_Post>> get_api_posts(
            HttpSession session,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort

    ) {
        return service_post.get_api_posts(session, page, size, sort);
    }

    @Operation(
            summary = "For You Page for current user",
            description = "Returns a paginated list of posts from accounts followed by the current logged-in user.",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)", required = false),
                    @Parameter(name = "size", description = "Page size", required = false),
                    @Parameter(name = "sort", description = "Sort order, e.g. 'createdDate,desc'", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Page of posts from followed accounts returned successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in"),
                    @ApiResponse(responseCode = "404", description = "User account not found")
            }
    )
    @GetMapping(value = "/api/fyp")
    public ResponseEntity<Page<DTO_Get_Post>> get_api_fyp(
            HttpSession session,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort

    ) {
        return service_post.get_api_fyp(session, page, size, sort);
    }

    // account's posts
    @Operation(
            summary = "Get posts by account ID",
            description = "Returns a paginated list of posts created by the specified account.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the account", required = true),
                    @Parameter(name = "page", description = "Page number (0-based)", required = false),
                    @Parameter(name = "size", description = "Page size", required = false),
                    @Parameter(name = "sort", description = "Sort order, e.g. 'createdDate,desc'", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Account not found")
            }
    )
    @GetMapping("/api/accounts/{id}/posts")
    public ResponseEntity<Page<DTO_Get_Post>> get_api_accounts_id_posts(
            HttpSession session,
            @PathVariable(name = "id") Long id,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        return service_post.get_api_accounts_id_posts(id, session, page, size, sort);
    }

    // get single post
    @Operation(
            summary = "Get single post info by post ID",
            description = "Retrieves detailed information for a single post given its ID. Returns 404 if the post is not found.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post found and returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DTO_Get_Post.class))),
                    @ApiResponse(responseCode = "404", description = "Post not found")
            }
    )
    @GetMapping("/api/posts/{id}")
    public ResponseEntity<DTO_Get_Post> get_api_posts_id(@PathVariable(name = "id") Long id, HttpSession session) {
        return service_post.get_api_posts_id(id, session);
    }

    @Operation(
            summary = "Clear location cache",
            description = "Evicts all cached location entries to force reload fresh data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Locations successfully removed from cache"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping(value = "/api/removeCache")
    public ResponseEntity<String> removeFromCache() {
        service_post.removeFromCache();
        return ResponseEntity.ok("Locations successfully removed from cache!");
    }

    @Operation(
            summary = "Mark post for advertising",
            description = "Marks a post to be sent to advertising agencies and triggers notification.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post checked for advertising"),
                    @ApiResponse(responseCode = "400", description = "Post doesn't exist"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in")
            }
    )
    @PostMapping(value="/api/posts/{id}/advertising")
    public ResponseEntity<String> post_api_advertising_post(@PathVariable(name="id") Long id, HttpSession session){
        service_post.post_api_send_post_to_advertising_agencies(id, session);
        return new ResponseEntity<>("Post checked for advertising.", HttpStatus.OK);
    }

    // CREATE POST
    @Operation(summary = "Create post", description = "Creates a new post with title, text, location, and optional image upload.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or location not found"),
            @ApiResponse(responseCode = "401", description = "Not logged in")
    })
    @PostMapping("/api/posts")
    @Timed(value = "create.post.request", description = "Time taken to create a post")
    public ResponseEntity<String> post_api_posts(@RequestParam("title") String title,
                                                 @RequestParam("text") String text,
                                                 @RequestParam("location") String location,
                                                 @RequestParam(value = "image", required = false) MultipartFile imageFile,
                                                 HttpSession session) {
        return service_post.post_api_posts(title, text, location, imageFile, session);
    }

    // GET REPLIES
    @Operation(
            summary = "Get post replies thread",
            description = "Returns the list of replies (thread) for a given post ID. " +
                    "Includes nested replies recursively.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of post replies",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DTO_Get_Post.class))
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Post not found")
            }
    )
    @GetMapping("/api/posts/{id}/thread")
    public ResponseEntity<List<DTO_Get_Post>> get_api_posts_id_thread(@PathVariable(name = "id") Long id, HttpSession session) {
        return service_post.get_api_posts_id_thread(id, session);
    }

    // POST REPLY
    @Operation(
            summary = "Reply to a post",
            description = "Adds a reply comment to the post identified by the given ID. Requires user to be logged in and respects rate limiting.",
            requestBody = @RequestBody(
                    description = "Reply content",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DTO_Post_Reply.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post commented successfully"),
                    @ApiResponse(responseCode = "401", description = "User not logged in"),
                    @ApiResponse(responseCode = "404", description = "Original post not found"),
                    @ApiResponse(responseCode = "429", description = "Comment limit reached, rate limited")
            }
    )
    @PostMapping("/api/posts/{id}/reply")
    public ResponseEntity<String> post_api_posts_id_reply(@PathVariable(name = "id") Long id, @RequestBody DTO_Post_Reply replyDTO, HttpSession session) {
        return service_post.post_api_posts_id_reply(id, replyDTO, session);
    }

    // LIKE POST
    @Operation(
            summary = "Like or unlike a post",
            description = "Toggles a like for the post identified by the given ID. User must be logged in. If the post is already liked by the user, this will unlike it.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post liked or unliked successfully"),
                    @ApiResponse(responseCode = "401", description = "User not logged in"),
                    @ApiResponse(responseCode = "404", description = "Post or user account not found")
            }
    )
    @PostMapping("/api/posts/{id}/like")
    public ResponseEntity<String> post_api_posts_id_like(@PathVariable(name = "id") Long id, HttpSession session) {
        return service_post.post_api_posts_id_like(id, session);
    }

    // GET LIKES
    @Operation(
            summary = "Get likes for a post",
            description = "Returns a list of likes for the post identified by the given ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of likes retrieved successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = DTO_Get_Like.class)))),
                    @ApiResponse(responseCode = "404", description = "Post not found")
            }
    )
    @GetMapping("/api/posts/{id}/likes")
    public ResponseEntity<List<DTO_Get_Like>> post_api_posts_id_likes(@PathVariable(name = "id") Long id) {
        return service_post.get_api_posts_id_likes(id);
    }

    // UPDATE POST
    @Operation(
            summary = "Update post",
            description = "Updates the post identified by the given ID. Only the owner of the post can update it.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = DTO_Put_Post.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post updated successfully"),
                    @ApiResponse(responseCode = "401", description = "Not logged in"),
                    @ApiResponse(responseCode = "403", description = "User does not own this post"),
                    @ApiResponse(responseCode = "404", description = "Post not found")
            }
    )
    @PutMapping("/api/posts/{id}")
    public ResponseEntity<String> put_api_posts_id(@PathVariable(name = "id") Long id, DTO_Put_Post dto_put_post, @RequestParam(value = "image", required = false) MultipartFile imageFile, HttpSession session) {
        return service_post.put_api_posts_id(id, dto_put_post, imageFile, session);
    }

    // DELETE POST
    @Operation(
            summary = "Delete post",
            description = "Deletes the post with the given ID if the current user is the owner.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post deleted."),
                    @ApiResponse(responseCode = "401", description = "Not logged in."),
                    @ApiResponse(responseCode = "403", description = "You don't own this post."),
                    @ApiResponse(responseCode = "404", description = "Post not found.")
            }
    )
    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<String> delete_api_posts_id(@PathVariable(name = "id") Long id, HttpSession session) {
        return service_post.delete_api_posts_id(id, session);
    }
}
