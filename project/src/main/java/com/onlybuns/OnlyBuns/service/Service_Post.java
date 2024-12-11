package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_Post_Reply;
import com.onlybuns.OnlyBuns.dto.DTO_Put_Post;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Like;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Post;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Like;
import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_Like;
import com.onlybuns.OnlyBuns.util.VarConverter;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional // TODO: change this to only be on top of the functions that actually need it (create things / have lazy fetching)
public class Service_Post {

    @Autowired
    private Repository_Account repository_account;

    @Autowired
    private Repository_Post repository_post;

    @Autowired
    private Repository_Like repository_like;

    public Optional<Post> findById(Long id) {
        return repository_post.findById(id);
    }

    @Autowired
    private Service_DiskWriter service_diskWriter;

    private final ConcurrentHashMap<Long, List<Instant>> userCommentTimestamps = new ConcurrentHashMap<>();
    private static final int MAX_COMMENTS_PER_HOUR = 60;

    private final VarConverter varConverter = new VarConverter();

    // /api/posts
    public ResponseEntity<Page<DTO_Get_Post>> get_api_posts(HttpSession session, Integer page, Integer size, String sort) {
        Account account = (Account) session.getAttribute("user");
        return new ResponseEntity<>(getPostsForUser(repository_post.findByParentPostIsNull(varConverter.pageable(page, size, sort)), account), HttpStatus.OK);
    }

    // /api/fyp
    public ResponseEntity<Page<DTO_Get_Post>> get_api_fyp(HttpSession session, Integer page, Integer size, String sort) {
        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }
        Optional<Account> optional_account = repository_account.findById(sessionAccount.getId());
        if (optional_account.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Account account = optional_account.get();

        Set<Account> followedAccounts = account.getFollowing(); // Get the accounts the user follows
        Pageable pageable = varConverter.pageable(page, size, sort);
        Page<Post> posts = repository_post.findPostsByFollowedAccounts(followedAccounts, pageable);
        return new ResponseEntity<>(getPostsForUser(posts, account), HttpStatus.OK);
    }

    // /api/posts/{id}
    public ResponseEntity<DTO_Get_Post> get_api_posts_id(Long id, HttpSession session) {
        Optional<Post> postOptional = repository_post.findById(id);
        if (postOptional.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        Post post = postOptional.get();

        Account account = (Account) session.getAttribute("user");
        return new ResponseEntity<>(getPostForUser(post, account, 0), HttpStatus.OK);
    }
    // /api/posts/{id}/thread
    public ResponseEntity<List<DTO_Get_Post>> get_api_posts_id_thread(Long id, HttpSession session) {
        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Post post = optional_post.get();
        Account account = (Account) session.getAttribute("user");
        List<DTO_Get_Post> thread = new ArrayList<>();
        getThreadForUser(thread, post, account, 0);
        return new ResponseEntity<>(thread, HttpStatus.OK);
    }

    // /accounts/{id}/posts
    public ResponseEntity<Page<DTO_Get_Post>> get_api_accounts_id_posts(Long id, HttpSession session, Integer page, Integer size, String sort) {
        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Account account = optional_account.get();
        Account sessionAccount = (Account) session.getAttribute("user");
        return new ResponseEntity<>(getPostsForUser(repository_post.findAllByAccount(account, varConverter.pageable(page, size, sort)), sessionAccount), HttpStatus.OK);
    }
    public void getThreadForUser(List<DTO_Get_Post> thread, Post post, Account account, Integer indent) {

        thread.add(getPostForUser(post, account, indent));

        List<Post> replies = post.getReplies();
        if (!replies.isEmpty()) {
            for (Post i : replies) {
                getThreadForUser(thread, i, account, indent + 20);
            }
        }
    }
    public DTO_Get_Post getPostForUser(Post post, Account account, Integer indent) {

        if (account == null) {
            return new DTO_Get_Post(post, false, false, indent);
        }

        return new DTO_Get_Post(
                post,
                repository_like.existsByPostAndAccount(post, account), // liked
                post.getAccount().getId().equals(account.getId()),  // myPost
                indent
        );
    }
    public Page<DTO_Get_Post> getPostsForUser(Page<Post> posts, Account account) {

        if (account == null) {
            return posts.map(post -> new DTO_Get_Post(post, false, false, 0));
        }

        List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());


        // Retrieve likes for the current user in a single query to avoid multiple calls
        List<Like> userLikes = repository_like.findByPostIdInAndAccount(postIds, account);
        Set<Long> likedPostIds = userLikes.stream()
                .map(like -> like.getPost().getId())
                .collect(Collectors.toSet());

        return posts.map(post -> new DTO_Get_Post(post, likedPostIds.contains(post.getId()), post.getAccount().getId().equals(account.getId()), 0));
    }
    public List<DTO_Get_Post> getPostsForUser(List<Post> posts, Account account) {

        if (account == null) {
            return posts.stream()
                    .map(post -> new DTO_Get_Post(post, false, false, 0))
                    .collect(Collectors.toList());
        }

        List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());

        // Retrieve likes for the current user in a single query to avoid multiple calls
        List<Like> userLikes = repository_like.findByPostIdInAndAccount(postIds, account);
        Set<Long> likedPostIds = userLikes.stream()
                .map(like -> like.getPost().getId())
                .collect(Collectors.toSet());

        return posts.stream()
                .map(post -> new DTO_Get_Post(post, likedPostIds.contains(post.getId()), post.getAccount().getId().equals(account.getId()), 0))
                .collect(Collectors.toList());
    }

    // create post
    public ResponseEntity<String> post_api_posts(String title, String text, String location, MultipartFile imageFile, HttpSession session) {

        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount == null) {
            return new ResponseEntity<>("Not logged in.", HttpStatus.UNAUTHORIZED);
        }

        // Validacija podataka
        if (
                title == null ||
                        title.isEmpty() ||
                        text == null ||
                        text.isEmpty() ||
                        location == null ||
                        location.isEmpty()
        ) {
            return new ResponseEntity<>("All fields are required.", HttpStatus.BAD_REQUEST);
        }

        String diskLocation = service_diskWriter.saveImage(imageFile);
        ;
        Post newPost = new Post(title, text, location, diskLocation, sessionAccount);
        repository_post.save(newPost);
        System.out.println("Post created: " + newPost);
        return new ResponseEntity<>("Post created successfully.", HttpStatus.OK);
    }

    // like / unlike post
    public ResponseEntity<String> post_api_posts_id_like(Long id, HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount == null) {
            return new ResponseEntity<>("Can't like when logged out.", HttpStatus.UNAUTHORIZED);
        }

        Optional<Account> optional_account = repository_account.findById(sessionAccount.getId());
        if (optional_account.isEmpty()) {
            return new ResponseEntity<>("Can't find your account.", HttpStatus.NOT_FOUND);
        }
        Account account = optional_account.get();

        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) {
            return new ResponseEntity<>("Can't find post.", HttpStatus.NOT_FOUND);
        }
        Post post = optional_post.get();

        synchronized (post) {
            Optional<Like> optional_like = repository_like.findByAccountIdAndPostId(account.getId(), post.getId());
            if (optional_like.isEmpty()) {

                // create new like
                Like newLike = new Like(account, post);
                post.getLikes().add(newLike);
                repository_like.save(newLike);
                post.incrementLikeCount();
                repository_post.save(post);

                return new ResponseEntity<>("Post liked.", HttpStatus.OK);
            }

            Like like = optional_like.get();
            repository_like.delete(like);
            post.decrementLikeCount();
            repository_post.save(post);

            return new ResponseEntity<>("Post unliked.", HttpStatus.OK);
        }
    }

    // get posts likes
    public ResponseEntity<List<DTO_Get_Like>> get_api_posts_id_likes(Long id) {
        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        Post post = optional_post.get();
        List<DTO_Get_Like> dtos = new ArrayList<>();
        for (Like i : post.getLikes()) {
            dtos.add(new DTO_Get_Like(i));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // reply to post
    public ResponseEntity<String> post_api_posts_id_reply(Long postId, DTO_Post_Reply replyDTO, HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount == null) {
            return new ResponseEntity<>("Can't comment when logged out.", HttpStatus.UNAUTHORIZED);
        }

        if (!canUserComment(sessionAccount.getId())) {
            return new ResponseEntity<>("Comment limit reached. Please wait before commenting again.", HttpStatus.TOO_MANY_REQUESTS);
        }

        Optional<Post> originalPost = repository_post.findById(postId);
        if (originalPost.isEmpty()) {
            return new ResponseEntity<>("Can't find post.", HttpStatus.NOT_FOUND);
        }

        Post parentPost = originalPost.get();
        Post reply = new Post(replyDTO.getTitle(), replyDTO.getText());
        reply.setAccount(sessionAccount);
        reply.setParentPost(parentPost);
        parentPost.getReplies().add(reply);

        repository_post.save(reply);
        addCommentTimestamp(sessionAccount.getId());

        return new ResponseEntity<>("Post commented.", HttpStatus.OK);
    }

    private boolean canUserComment(Long userId) {
        List<Instant> timestamps = userCommentTimestamps.getOrDefault(userId, new ArrayList<>());
        Instant oneHourAgo = Instant.now().minusSeconds(3600);

        timestamps.removeIf(timestamp -> timestamp.isBefore(oneHourAgo));

        userCommentTimestamps.put(userId, timestamps);

        return timestamps.size() < MAX_COMMENTS_PER_HOUR;
    }

    private void addCommentTimestamp(Long userId) {
        List<Instant> timestamps = userCommentTimestamps.getOrDefault(userId, new ArrayList<>());
        timestamps.add(Instant.now());
        userCommentTimestamps.put(userId, timestamps);
    }

    // update post
    public ResponseEntity<String> put_api_posts_id(@PathVariable(name = "id") Long id, DTO_Put_Post dto_put_post, MultipartFile imageFile, HttpSession session) {

        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount == null) {
            return new ResponseEntity<>("Not logged in.", HttpStatus.UNAUTHORIZED);
        }
        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) {
            return new ResponseEntity<>("Can't find post.", HttpStatus.NOT_FOUND);
        }
        Post post = optional_post.get();
        if (!post.getAccount().getId().equals(sessionAccount.getId())) {
            return new ResponseEntity<>("You don't own this post.", HttpStatus.FORBIDDEN);
        }

        Post existingPost = optional_post.get();
        String filePath = existingPost.getPictureUrl();
        String oldFilePath = existingPost.getPictureLocation();

        if (imageFile != null && !imageFile.isEmpty()) {
            String diskLocation = service_diskWriter.saveImage(imageFile);
            existingPost.setImageLocationAndUrl(diskLocation);
            service_diskWriter.deleteImage(oldFilePath);
        }
        existingPost.setTitle(dto_put_post.getTitle());
        existingPost.setText(dto_put_post.getText());
        existingPost.setLocation(dto_put_post.getLocation());


        repository_post.save(existingPost);
        System.out.println("Post updated: " + existingPost);
        return new ResponseEntity<>("Post updated successfully.", HttpStatus.OK);
    }

    // delete post
    public ResponseEntity<String> delete_api_posts_id(@PathVariable(name = "id") Long id, HttpSession session) {

        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount == null) {
            return new ResponseEntity<>("Not logged in.", HttpStatus.UNAUTHORIZED);
        }
        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) {
            return new ResponseEntity<>("Can't find post.", HttpStatus.NOT_FOUND);
        }
        Post post = optional_post.get();
        if (!post.getAccount().getId().equals(sessionAccount.getId())) {
            return new ResponseEntity<>("You don't own this post.", HttpStatus.FORBIDDEN);
        }

        // delete post
        repository_post.delete(post);

        // remove post's image if exists
        service_diskWriter.deleteImage(post.getPictureLocation());

        return new ResponseEntity<>("Post deleted.", HttpStatus.OK);
    }
}
