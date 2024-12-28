package com.onlybuns.OnlyBuns.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private String content;

    @Column
    private Message_Type type = Message_Type.MESSAGE;

    @CreationTimestamp
    private LocalDateTime createdDate;

    public Message(Chat chat, Account account, String content, Message_Type type) {
        this.chat = chat;
        this.account = account;
        this.content = content;
        this.type = type;
    }
}
