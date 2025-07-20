package com.onlybuns.OnlyBuns;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.service.Service_Account;
import com.onlybuns.OnlyBuns.service.Service_Post;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestLikes {


    @Autowired
    private Service_Post service_post;

    @Autowired
    private Service_Account service_account;

    private Post savedPost;

    @Before
    public void setUp() throws Exception {
        Account account = new Account(1L,"test1@example.com", "testUserName1", "password1");
        var savedAccount = service_account.save(account.getId());
        savedPost = service_post.save(new Post("P1", "O1", savedAccount));
    }

    @Test(expected = ObjectOptimisticLockingFailureException.class)
    public void testOptimisticLockingScenario() throws Throwable {

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<?> future1 = executor.submit(new Runnable() {

            @Override
            public void run() {
                System.out.println("Startovan Thread 1");
                Post postToUpdate = service_post.findById(savedPost.getId()).get();
                postToUpdate.setLikesCount(3);
                try { Thread.sleep(3000); } catch (InterruptedException e) {}
                service_post.save(postToUpdate);

            }
        });
        executor.submit(new Runnable() {

            @Override
            public void run() {
                System.out.println("Startovan Thread 2");
                Post postToUpdate = service_post.findById(savedPost.getId()).get();
                postToUpdate.setLikesCount(4);
                service_post.save(postToUpdate);
            }
        });
        try {
            future1.get();
        } catch (ExecutionException e) {
            System.out.println("Exception from thread " + e.getCause().getClass());
            throw e.getCause();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();

    }

}
