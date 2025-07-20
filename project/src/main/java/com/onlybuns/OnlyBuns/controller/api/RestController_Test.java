package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.service.Service_ScheduleCleanup;
import com.onlybuns.OnlyBuns.service.Service_Test;
import com.onlybuns.OnlyBuns.util.FollowRateLimiter;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_Follow;
import com.onlybuns.OnlyBuns.service.Service_Account;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class RestController_Test {

    @Autowired
    private Service_Test service_test;

    @Autowired
    private Service_ScheduleCleanup scheduledTask;

    @Autowired
    private FollowRateLimiter followRateLimiter;

    @Autowired
    private Repository_Follow repository_follow;

    @Autowired
    private Service_Account service_Account;

    @Autowired
    private Repository_Account repository_account;

    @io.swagger.v3.oas.annotations.Operation(
            summary = "Test image compression",
            description = "Endpoint to test image compression functionality.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Test executed successfully.")
            }
    )
    @GetMapping("/api/test")
    public ResponseEntity<String> get_api_test() {
        return service_test.get_api_test();
    }

    @io.swagger.v3.oas.annotations.Operation(
            summary = "Test account cleanup cron job",
            description = "Manually triggers the scheduled account cleanup task that deletes unactivated accounts.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Account cleanup task executed successfully."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error executing account cleanup task.")
            }
    )
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

    @io.swagger.v3.oas.annotations.Operation(
            summary = "Test follow rate limiting with 51 accounts",
            description = "Simulates following 55 accounts, waits 60 seconds, then attempts to follow 51 more to test rate limiting and reset behavior.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Test completed successfully."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error during test execution.")
            }
    )
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

    @io.swagger.v3.oas.annotations.Operation(
            summary = "Test concurrent follow operations",
            description = "Simulates two accounts concurrently following the same target account to test transactional behavior and concurrency handling.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Test completed successfully, returns detailed result summary."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error during test execution.")
            }
    )
    @Transactional
    @GetMapping("/api/test/concurrent-follows")
    public ResponseEntity<String> testConcurrentFollows() {

        try {

            Account target = repository_account.findById(1L).orElseGet(() ->

                    repository_account.save(
                            new Account(1L, "target_account", "target@test.com", "targetpass")
                    ));

            Account follower1 = repository_account.findById(2L).orElseGet(() ->

                    repository_account.save(
                            new Account(2L, "follower1", "follower1@test.com", "follower1pass")
                    ));

            Account follower2 = repository_account.findById(3L).orElseGet(() ->
                    repository_account.save(
                            new Account(3L, "follower2", "follower2@test.com", "follower2pass")
                    ));

            repository_follow.deleteByFollowee(target);

            ExecutorService executor = Executors.newFixedThreadPool(2);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);

            executor.submit(() -> {
                try {
                    service_Account.followTransactional(follower1, target);
                    successCount.incrementAndGet();
                    Thread.sleep(2000);
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.err.println("Thread 1 failed: " + e.getMessage());
                }
            });

            executor.submit(() -> {
                try {
                    Thread.sleep(500);
                    service_Account.followTransactional(follower2, target);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.err.println("Thread 2 failed: " + e.getMessage());
                }
            });
            executor.shutdown();
            boolean finished = executor.awaitTermination(5, TimeUnit.SECONDS);
            if (!finished) {
                executor.shutdownNow();
            }

            int dbCount = repository_follow.countByFollowee(target);
            boolean relationship1Exists = repository_follow.existsByFollowerAndFollowee(follower1, target.getId());
            boolean relationship2Exists = repository_follow.existsByFollowerAndFollowee(follower2, target.getId());

            String result = String.format(
                    "Concurrent Follow Test Results:%n" +
                            "- Successful operations: %d%n" +
                            "- Failed operations: %d%n" +
                            "- Database follower count: %d%n" +
                            "- Follower1 relationship exists: %b%n" +
                            "- Follower2 relationship exists: %b%n" +
                            "- Expected count: 2",
                    successCount.get(),
                    failureCount.get(),
                    dbCount,
                    relationship1Exists,
                    relationship2Exists
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Test failed completely: " + e.getMessage());
        }
    }
}
