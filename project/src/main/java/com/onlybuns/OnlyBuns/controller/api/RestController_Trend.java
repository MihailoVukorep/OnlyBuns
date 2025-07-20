package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Account;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Post;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Trend;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Trend_Counts;
import com.onlybuns.OnlyBuns.service.Service_Trend;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary = "Get trends",
            description = "Returns the current trend data including weekly top posts, all-time top posts, and most active likers for the logged-in user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trend data retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO_Get_Trend.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in")
            }
    )
    @GetMapping("/api/trends")
    public ResponseEntity<DTO_Get_Trend> get_api_trends(HttpSession session) {
        return service_trend.get_api_trends(session);
    }

    @Operation(
            summary = "Get trend counts",
            description = "Returns summary counts of the current trends for the logged-in user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trend counts retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO_Get_Trend_Counts.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in")
            }
    )
    @GetMapping("/api/trends/counts")
    public ResponseEntity<DTO_Get_Trend_Counts> get_api_trends_counts(HttpSession session) {
        return service_trend.get_api_trends_counts(session);
    }

    @Operation(
            summary = "Get top weekly posts",
            description = "Retrieve the top weekly posts based on current trends for the logged-in user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Top weekly posts retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DTO_Get_Post.class))
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in")
            }
    )
    @GetMapping("/api/trends/weekly")
    public ResponseEntity<List<DTO_Get_Post>> get_api_trends_weekly(HttpSession session) {
        return service_trend.get_api_trends_weekly(session);
    }

    @Operation(
            summary = "Get top all-time posts",
            description = "Retrieve the top all-time posts based on current trends for the logged-in user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Top all-time posts retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DTO_Get_Post.class))
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in")
            }
    )
    @GetMapping("/api/trends/top")
    public ResponseEntity<List<DTO_Get_Post>> get_api_trends_top(HttpSession session) {
        return service_trend.get_api_trends_top(session);
    }

    @Operation(
            summary = "Get top likers",
            description = "Retrieve the list of top likers based on current trends for the logged-in user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Top likers retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DTO_Get_Account.class))
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in")
            }
    )
    @GetMapping("/api/trends/likers")
    public ResponseEntity<List<DTO_Get_Account>> get_api_trends_likers(HttpSession session) {
        return service_trend.get_api_trends_likers(session);
    }
}
