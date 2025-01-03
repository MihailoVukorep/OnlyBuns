package com.onlybuns.OnlyBuns.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token = UUID.randomUUID().toString();

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Account admin;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChatMember> members = new ArrayList<>();

    public Chat(Account admin, String name) {
        this.admin = admin;
        this.name = name;
    }

    public void addMember(ChatMember member) {
        this.members.add(member);
    }

    public void removeMember(ChatMember member) {
        this.members.remove(member);
    }

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Message> messages;

    @CreationTimestamp
    private LocalDateTime createdDate;
}