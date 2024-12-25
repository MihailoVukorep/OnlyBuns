package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Location;
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

    public ResponseEntity<DTO_Get_Location> get_api_map_locations(HttpSession session) {

        Account account = (Account) session.getAttribute("user");
        if (account == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        List<String> coordinates = new ArrayList<>();
        for (Post i : repository_post.findAll()) {
            coordinates.add(i.getLocation());
        }

        return new ResponseEntity<>(new DTO_Get_Location(account.getAddress(), coordinates), HttpStatus.OK);
    }
}
