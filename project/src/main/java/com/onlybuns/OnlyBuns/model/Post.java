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
    private String picture;

    @Column
    private String location;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "parentPost", cascade = CascadeType.REFRESH, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Post> replies = new ArrayList<>(); // This will hold replies to the post

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_post_id")
    private Post parentPost; // This will reference the parent post (if any)

    @OneToMany(mappedBy = "post", cascade = CascadeType.REFRESH, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Like> likes = new ArrayList<>();;

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

    public Post(String title, String text, String location, String picture, Account account) {
        this.title = title;
        this.text = text;
        this.location = location;
        this.picture = picture;
        this.account = account;
    }

    // Constructor for replies (setting parent post)
    public Post(String title, String text, Account account, Post parentPost) {
        this.title = title;
        this.text = text;
        this.account = account;
        this.parentPost = parentPost;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", location='" + location + '\'' +
                ", picture='" + picture + '\'' +
                ", account=" + account.getId() +
                ", replies=" + replies.size() +
                ", likes=" + likes.size() +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
