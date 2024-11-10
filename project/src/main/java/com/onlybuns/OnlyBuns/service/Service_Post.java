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
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class Service_Post {

    @Autowired
    private Repository_Account repository_account;

    @Autowired
    private Repository_Post repository_post;

    @Autowired
    private Repository_Like repository_like;

    private final VarConverter varConverter = new VarConverter();

    // GETTING POSTS
    public ResponseEntity<List<DTO_Get_Post>> get_api_posts(HttpSession session, String sort) {
        return new ResponseEntity<>(get_api_posts_raw(session, sort), HttpStatus.OK);
    }
    public List<DTO_Get_Post> get_api_posts_raw(HttpSession session, String sort) {
        Sort sortOrder = varConverter.parseSort(sort);
        Account account = (Account) session.getAttribute("user");
        return  getPostsForUser(repository_post.findByParentPostIsNull(sortOrder), account);
    }
    public ResponseEntity<DTO_Get_Post> get_api_posts_id(Long id, HttpSession session) {
        Optional<Post> postOptional = repository_post.findById(id);
        if (postOptional.isEmpty()) {return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Post post = postOptional.get();

        Account account = (Account) session.getAttribute("user");
        return new ResponseEntity<>(getPostForUser(post, account), HttpStatus.OK);
    }
    public ResponseEntity<List<DTO_Get_Post>> get_api_posts_id_replies(Long id, HttpSession session) {
        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Post post = optional_post.get();

        Account account = (Account) session.getAttribute("user");
        return new ResponseEntity<>(getPostsForUser(post.getReplies(), account), HttpStatus.OK);
    }
    public DTO_Get_Post getPostForUser(Post post, Account account) {

        if (account == null) {
            return new DTO_Get_Post(post, false, false);
        }

        return new DTO_Get_Post(
                post,
                repository_like.existsByPostAndAccount(post, account), // liked
                post.getAccount().getId().equals(account.getId())  // myPost
            );
    }
    public List<DTO_Get_Post> getPostsForUser(List<Post> posts, Account account) {

        if (account == null) {
            return posts.stream()
                    .map(post -> new DTO_Get_Post(post, false, false))
                    .collect(Collectors.toList());
        }

        List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());

        // Retrieve likes for the current user in a single query to avoid multiple calls
        List<Like> userLikes = repository_like.findByPostIdInAndAccount(postIds, account);
        Set<Long> likedPostIds = userLikes.stream()
                .map(like -> like.getPost().getId())
                .collect(Collectors.toSet());

        return posts.stream()
                .map(post -> new DTO_Get_Post(
                        post,
                        likedPostIds.contains(post.getId()),
                        post.getAccount().getId().equals(account.getId()))
                )
                .collect(Collectors.toList());
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/" + filePath.toString();
    }

    // CREATING POSTS
    @Transactional
    public ResponseEntity<String> post_api_createpost(String title, String description, String location, MultipartFile imageFile, HttpSession session) {

        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount == null) { return new ResponseEntity<>("Not logged in.", HttpStatus.UNAUTHORIZED); }

        // Validacija podataka
        if (
                title == null ||
                        title.isEmpty() ||
                        description == null ||
                        description.isEmpty() ||
                        location == null ||
                        location.isEmpty())
        {
            return new ResponseEntity<>("All fields are required.", HttpStatus.BAD_REQUEST);
        }
        String filePath = null;  // Inicijalizacija filePath varijable

        try {
            // Save the file to the directory
            filePath = saveImage(imageFile);
            //return ResponseEntity.ok("Image uploaded successfully: " + filePath);
        }
        catch (IOException ignored) {

            // post without image
            //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
        catch (Exception ignored) {

        }

        // Kreiranje posta
        Post newPost = new Post(title, description, location, filePath, sessionAccount);
        repository_post.save(newPost);

        System.out.println("Post created: " + newPost);
        if (filePath != null) {
            System.out.println("Image path: " + filePath);
        }

        return new ResponseEntity<>("Post created successfully.", HttpStatus.OK);
    }


    // LIKE
    @Transactional
    public ResponseEntity<String> post_api_posts_id_like(Long id, HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount == null) { return new ResponseEntity<>("Can't like when logged out.", HttpStatus.BAD_REQUEST); }

        Optional<Account> optional_account = repository_account.findById(sessionAccount.getId());
        if (optional_account.isEmpty()) { return new ResponseEntity<>("Can't find your account.", HttpStatus.NOT_FOUND); }
        Account account = optional_account.get();

        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) { return new ResponseEntity<>("Can't find post.", HttpStatus.NOT_FOUND); }
        Post post = optional_post.get();

        Optional<Like> optional_like = repository_like.findByAccountIdAndPostId(account.getId(), post.getId());
        if (optional_like.isEmpty()) {

            // create new like
            Like newLike = new Like(account, post);
            post.getLikes().add(newLike);
            repository_like.save(newLike);

            return new ResponseEntity<>("Post liked.", HttpStatus.OK);
        }

        Like like = optional_like.get();
        repository_like.delete(like);

        return new ResponseEntity<>("Post unliked.", HttpStatus.OK);
    }

    // GET LIKES
    public ResponseEntity<List<DTO_Get_Like>> get_api_posts_id_likes(Long id) {
        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Post post = optional_post.get();
        List<DTO_Get_Like> dtos = new ArrayList<>();
        for (Like i : post.getLikes()) { dtos.add(new DTO_Get_Like(i)); }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // COMMENT
    @Transactional
    public ResponseEntity<String> post_api_posts_id_replies(Long postId, DTO_Post_Reply replyDTO, HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount == null) {
            return new ResponseEntity<>("Can't comment when logged out.", HttpStatus.UNAUTHORIZED);
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

        return new ResponseEntity<>("Post commented.", HttpStatus.OK);
    }

    // UPDATE POST
    public ResponseEntity<String> put_api_posts_id(@PathVariable(name = "id") Long id, DTO_Put_Post dto_put_post, MultipartFile imageFile, HttpSession session) {

        Optional<Post> existingPostOpt = repository_post.findById(id);
        if (existingPostOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }

        Post existingPost = existingPostOpt.get();
        String filePath = existingPost.getPicture();

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                filePath = saveImage(imageFile);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
            }
        }
        existingPost.setTitle(dto_put_post.getTitle());
        existingPost.setText(dto_put_post.getText());
        existingPost.setLocation(dto_put_post.getLocation());
        existingPost.setPicture(filePath);

        repository_post.save(existingPost);
        System.out.println("Post updated: " + existingPost);
        if (filePath != null) {
            System.out.println("Image path: " + filePath);
        }
        return new ResponseEntity<>("Post updated successfully.", HttpStatus.OK);
    }

    // DELETE POST
    @Transactional
    public ResponseEntity<String> delete_api_posts_id(@PathVariable(name = "id") Long id, HttpSession session) {

        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount == null) { return new ResponseEntity<>("Not logged in.", HttpStatus.UNAUTHORIZED); }

        Optional<Post> optional_post = repository_post.findById(id);
        if (optional_post.isEmpty()) { return new ResponseEntity<>("Can't find post.", HttpStatus.NOT_FOUND); }
        Post post = optional_post.get();

        if (!post.getAccount().getId().equals(sessionAccount.getId())) {
            return new ResponseEntity<>("You don't own this post.", HttpStatus.FORBIDDEN);
        }

        repository_post.delete(post);
        return new ResponseEntity<>("Post deleted.", HttpStatus.OK);
    }

    @Scheduled(cron = "0 19 3 * * *")
    public void compressOldImages() {
        List<Post> allPosts = repository_post.findAll();
        LocalDateTime oneMonthAgo = LocalDateTime.now().minus(1, ChronoUnit.MINUTES);

        for (Post post : allPosts) {
            if (post.getPicture() != null && post.getCreatedDate().isBefore(oneMonthAgo)) {
                try {
                    String baseDir = System.getProperty("user.dir");
                    String relativePath = post.getPicture();

                    Path inputPath = Paths.get(baseDir, relativePath.replaceFirst("^/", ""));  // Uklonite početni "/" ako postoji
                    File inputImage = inputPath.toFile();

                    if (inputImage.exists()) {
                    } else {
                        log.warn("File for Post ID {} not found: {}", post.getId(), post.getPicture());
                    }
                    String fileName = inputImage.getName();

                    Path outputDir = Paths.get("uploads/compressed");

                    if (!Files.exists(outputDir)) {
                        Files.createDirectories(outputDir);
                    }

                    File outputImage = outputDir.resolve(fileName).toFile();

                    Thumbnails.of(inputImage)
                            .scale(1)
                            .outputQuality(0.3)
                            .toFile(outputImage);

                    log.info("Image for post ID " + post.getId() + " is compressed.");

                } catch (IOException e) {
                    log.error("Error compressing image ID {}: {}", post.getId(), e.getMessage());
                }
            }
        }
    }
}
