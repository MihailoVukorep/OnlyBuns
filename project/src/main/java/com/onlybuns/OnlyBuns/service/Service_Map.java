package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Location;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Location_Type;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Locations;
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

    @Autowired
    private Service_RabbitCareMessageQueue queue;

    public ResponseEntity<DTO_Get_Locations> get_api_map_locations(HttpSession session) {

        Account account = (Account) session.getAttribute("user");
        if (account == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        // add posts coords to locations
        List<DTO_Get_Location> locations = new ArrayList<>();
        for (Post i : repository_post.findAll()) {
            String a = "/posts/" + i.getId();
            locations.add(new DTO_Get_Location(a, a, i.getLocation(), DTO_Get_Location_Type.POST));
        }

        locations.addAll(queue.getAllMessages());

        // // TEMP HARDCODED LOCATIONS
        // locations.add((new DTO_Get_Location("shelter/1", "hc shelter 1", "45.257026373153415,19.833277742498918", DTO_Get_Location_Type.SHELTER)));
        // locations.add((new DTO_Get_Location("shelter/2", "hc shelter 2", "45.23924745297107,19.840661218653235", DTO_Get_Location_Type.SHELTER)));
        // locations.add((new DTO_Get_Location("vet/1", "hc vec 2", "45.23552480014842,19.801602339283836", DTO_Get_Location_Type.VETERINARIAN)));
        // locations.add((new DTO_Get_Location("vet/2", "hc vet 2", "45.288969219446976,19.819460049052395", DTO_Get_Location_Type.VETERINARIAN)));

        return new ResponseEntity<>(new DTO_Get_Locations(account.getAddress(), locations), HttpStatus.OK);
    }
}
