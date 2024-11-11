package com.onlybuns.OnlyBuns.service;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountActivation;
import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_AccountActivation;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class Service_Test {


    @Autowired
    private Repository_Account repository_account;

    @Autowired
    private Repository_Post repository_post;

    @Autowired
    private Repository_AccountActivation repository_accountActivation;

    @Autowired
    private Service_DiskWriter service_diskWriter;

    public ResponseEntity<String> get_api_test() {

        List<Post> allPosts = repository_post.findAll();

        for (Post post : allPosts) {
            if (post.getPictureLocation() != null) {
                service_diskWriter.compressImage(post.getPictureLocation());
            }
        }

        return new ResponseEntity<>("testing", HttpStatus.OK);
    }
}
