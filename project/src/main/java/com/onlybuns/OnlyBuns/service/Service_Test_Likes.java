package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class Service_Test_Likes {

    @Autowired
    private Repository_Post postRepository;

    @Autowired
    private Service_Post postService;


    public ResponseEntity<String> get_api_conccurent_likes() throws Throwable {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<?> future1 = executor.submit(() -> {
            System.out.println("Started Thread 1");
            Post postToUpdate = postRepository.findById(Long.valueOf(3)).orElse(null);
            if (postToUpdate != null) {
                postToUpdate.incrementLikeCount();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                postRepository.save(postToUpdate);
            }
        });

        executor.submit(() -> {
            System.out.println("Started Thread 2");
            Post postToUpdate = postRepository.findById(Long.valueOf(3)).orElse(null);
            if (postToUpdate != null) {
                postToUpdate.incrementLikeCount();
                postRepository.save(postToUpdate);
            }
        });

        try {
            future1.get();
        } catch (Exception e) {
            System.out.println("Exception from thread: " + e.getCause().getClass());
            throw e.getCause();
        }

        executor.shutdown();
        return ResponseEntity.ok("Successfully tested concurrent likes.");
    }
}
