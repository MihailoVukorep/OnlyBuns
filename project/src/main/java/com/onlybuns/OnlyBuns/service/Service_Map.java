package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Service_Map {

    @Autowired
    private Repository_Post repository_post;

    public ResponseEntity<List<String>> get_api_map_locations(HttpSession session) {

        Account account = (Account) session.getAttribute("user");
        if (account == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        List<String> locations = new ArrayList<>();
        for (Post i : repository_post.findAll()) {
            locations.add(i.getLocation());
        }
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }
}
