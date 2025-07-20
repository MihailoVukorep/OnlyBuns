package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Account;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Analytics;
import com.onlybuns.OnlyBuns.service.Service_Admin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary = "Listing of all accounts on platform",
            description = "Returns a paginated list of accounts filtered by optional parameters such as firstName, lastName, userName, email, address, and post count range.",
            parameters = {
                    @Parameter(name = "firstName", description = "Filter by first name", required = false, schema = @Schema(type = "string")),
                    @Parameter(name = "lastName", description = "Filter by last name", required = false, schema = @Schema(type = "string")),
                    @Parameter(name = "userName", description = "Filter by username", required = false, schema = @Schema(type = "string")),
                    @Parameter(name = "email", description = "Filter by email", required = false, schema = @Schema(type = "string")),
                    @Parameter(name = "address", description = "Filter by address", required = false, schema = @Schema(type = "string")),
                    @Parameter(name = "minPostCount", description = "Minimum post count filter", required = false, schema = @Schema(type = "integer", format = "int32")),
                    @Parameter(name = "maxPostCount", description = "Maximum post count filter", required = false, schema = @Schema(type = "integer", format = "int32")),
                    @Parameter(name = "pageNum", description = "Page number (zero-based)", required = false, schema = @Schema(type = "integer", format = "int32", defaultValue = "0"))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of accounts retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DTO_Get_Account.class))
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - admin access required"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
            }
    )
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

    @Operation(
            summary = "Listing platform analytics",
            description = "Returns aggregated statistics such as post and comment counts for the current week, month, year, and user activity percentages.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Platform analytics retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO_Get_Analytics.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - admin access required"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
            }
    )
    @GetMapping("/api/admin/analytics")
    public ResponseEntity<DTO_Get_Analytics> get_api_analytics(){
        DTO_Get_Analytics analytics = service_admin.getAnalytics();
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }
}
