package com.onlybuns.OnlyBuns.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String text;

    @Column
    private String pictureLocation;

    @Column
    private String pictureUrl;

    @Column
    private String location;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "parentPost", cascade = CascadeType.REFRESH, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> replies = new ArrayList<>(); // This will hold replies to the post

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_post_id")
    private Post parentPost; // This will reference the parent post (if any)


    @Version
    private Long version;

    @Column
    private int likesCount = 0;
    public synchronized void incrementLikeCount() { this.likesCount++; }
    public synchronized void decrementLikeCount() { this.likesCount--; }

    @OneToMany(mappedBy = "post", cascade = CascadeType.REFRESH, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Like> likes = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

    public Post(String text, Account account) {
        this.text = text;
        this.account = account;
    }

    public Post(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public Post(String title, String text, Account account) {
        this.title = title;
        this.text = text;
        this.account = account;
    }

    public Post(String title, String text, String location, Account account) {
        this.title = title;
        this.text = text;
        this.location = location;
        this.account = account;
    }

    public Post(String title, String text, String location, String pictureLocation, Account account) {
        this.title = title;
        this.text = text;
        this.location = location;
        setImageLocationAndUrl(pictureLocation);
        this.account = account;
    }

    // Constructor for replies (setting parent post)
    public Post(String title, String text, Account account, Post parentPost) {
        this.title = title;
        this.text = text;
        this.account = account;
        this.parentPost = parentPost;
    }

    public Post(String title, String text, String location, Account account, Post parentPost) {
        this.title = title;
        this.text = text;
        this.location = location;
        this.account = account;
        this.parentPost = parentPost;
    }

    public void setImageLocationAndUrl(String imageLocationOnDisk) {
        if (imageLocationOnDisk == null) {
            return;
        }
        this.pictureLocation = imageLocationOnDisk;
        this.pictureUrl = "/" + imageLocationOnDisk;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", pictureLocation='" + pictureLocation + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", location='" + location + '\'' +
                ", account=" + account +
                ", replies=" + replies.size() +
//                ", parentPost=" + parentPost +
                ", likes=" + likes.size() +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
