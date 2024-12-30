package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Message;
import com.onlybuns.OnlyBuns.model.Message_Type;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DTO_Get_Message {

    //public Chat chat;
    public DTO_Get_Message_Account account;
    public String content;
    public LocalDateTime createdDate;
    public Message_Type type;
    public String createdDateStr;

    public DTO_Get_Message(Message message) {
        //this.chat = message.getChat();
        this.account = new DTO_Get_Message_Account(message.getAccount());
        this.content = message.getContent();
        this.type = message.getType();

        switch (type) {
            case JOINED:       this.content = "joined.";       break;
            case LEFT:         this.content = "left.";         break;
            case ADDED:        this.content = "added.";        break;
            case REMOVED:      this.content = "removed.";      break;
            case CONNECTED:    this.content = "connected.";    break;
            case DISCONNECTED: this.content = "disconnected."; break;
        }

        this.createdDate = message.getCreatedDate();
        this.createdDateStr = this.createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
