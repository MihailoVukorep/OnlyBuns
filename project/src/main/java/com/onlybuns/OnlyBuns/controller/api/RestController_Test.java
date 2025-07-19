package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.service.Service_ScheduleCleanup;
import com.onlybuns.OnlyBuns.service.Service_Test;
import com.onlybuns.OnlyBuns.service.Service_Test_Likes;
import com.onlybuns.OnlyBuns.util.FollowRateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RestController_Test {

    @Autowired
    private Service_Test service_test;

    @Autowired
    private Service_Test_Likes service_test_likes;

    @Autowired
    private Service_ScheduleCleanup scheduledTask;

    @Autowired
    private FollowRateLimiter followRateLimiter;

    @Operation(summary = "test image compression")
    @GetMapping("/api/test")
    public ResponseEntity<String> get_api_test() {
        return service_test.get_api_test();
    }

    @Operation(summary = "test account cleanup cron job")
    @GetMapping("/api/test/cleanup")
    public ResponseEntity<String> triggerAccountCleanup() {
        try {
            scheduledTask.deleteUnactivatedAccounts();
            return ResponseEntity.ok("Account cleanup task executed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error executing account cleanup task: " + e.getMessage());
        }
    }

    @Operation(summary = "Test follow rate limiting with 51 accounts")
    @GetMapping("/api/test/follow/bot-test")
    public ResponseEntity<String> testFollowRateLimitWithWait() {
        try {
            followRateLimiter.clear(1L);
            List<String> results = new ArrayList<>();

            results.add("=== First Attempt ===");
            int firstAttemptSuccess = 0;
            for (long i = 1; i <= 55; i++) {
                if (followRateLimiter.canFollow(1L)) {
                    firstAttemptSuccess++;
                    results.add("✅ Followed account: " + i);
                } else {
                    results.add("❌ Blocked account: " + i + " (rate limit exceeded)");
                }
            }

            // wait for 1 minute
            results.add("\n⏳ Waiting 60 seconds for rate limit to reset...");
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Wait interrupted: " + e.getMessage());
            }

            results.add("\n=== Second Attempt After Wait ===");
            int secondAttemptSuccess = 0;
            for (long i = 51; i <= 101; i++) {
                if (followRateLimiter.canFollow(1L)) {
                    secondAttemptSuccess++;
                    results.add("✅ Followed account: " + i);
                } else {
                    results.add("❌ Blocked account: " + i + " (rate limit exceeded)");
                }
            }

            String summary = String.format(
                    "\n\n=== Test Summary ===" +
                            "\nFirst Attempt Successful: %d" +
                            "\nFirst Attempt Blocked: %d" +
                            "\nSecond Attempt Successful: %d" +
                            "\nSecond Attempt Blocked: %d" +
                            "\nExpected Behavior: Should allow follows again after 1 minute wait",
                    firstAttemptSuccess, 51 - firstAttemptSuccess,
                    secondAttemptSuccess, 51 - secondAttemptSuccess
            );

            return ResponseEntity.ok(String.join("\n", results) + summary);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error during test: " + e.getMessage());
        }
    }
    /*@GetMapping("/api/test/conccurent")
    public ResponseEntity<String> get_api_conccurent_likes() {
        try {
            return service_test_likes.get_api_conccurent_likes();
        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Došlo je do greške: " + t.getMessage());
        }
    }*/
}
