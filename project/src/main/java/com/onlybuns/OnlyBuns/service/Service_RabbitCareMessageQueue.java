package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Location;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Service_RabbitCareMessageQueue {

    private final List<DTO_Get_Location> messageQueue = new ArrayList<>();

    public void addMessage(DTO_Get_Location dto) {
        messageQueue.add(dto);
    }

    public List<DTO_Get_Location> getAllMessages() {
        return new ArrayList<>(messageQueue); // kopija
    }
}
