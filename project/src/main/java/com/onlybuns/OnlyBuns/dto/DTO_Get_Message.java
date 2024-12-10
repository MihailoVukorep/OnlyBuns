package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Message;

import java.time.LocalDateTime;

public class DTO_Get_Message {

    //public Chat chat;
    public DTO_Get_Message_Account account;
    public String content;
    public LocalDateTime createdDate;

    public DTO_Get_Message(Message message) {
        //this.chat = message.getChat();
        this.account = new DTO_Get_Message_Account(message.getAccount());
        this.content = message.getContent();
        this.createdDate = message.getCreatedDate();
    }
}
