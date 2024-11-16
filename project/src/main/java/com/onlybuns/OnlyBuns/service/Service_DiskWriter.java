package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class Service_DiskWriter {

    @Autowired
    private Repository_Post repository_post;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveImage(MultipartFile file) {
        try {
            if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
                return null;
            }

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate a unique filename with a UUID
            String uniqueFileName;
            Path filePath;

            // Loop until we find a unique file name that does not exist
            do {
                uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                filePath = uploadPath.resolve(uniqueFileName);
            } while (Files.exists(filePath)); // Check for uniqueness

            // Copy file to the target location
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("saveImage: " + filePath);
            return filePath.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteImage(String path) {
        try {
            System.out.println("deleteImage: " + path);
            Files.delete(Paths.get(path));
        } catch (Exception e) {
            System.out.println("deleteImage fail: " + e.getMessage());

        }
    }

    public String compressImage(String imagePath) {
        try {
            // Get the original file
            File originalFile = new File(imagePath);

            // Extract the file name and extension
            String fileName = originalFile.getName();
            int dotIndex = fileName.lastIndexOf(".");
            String nameWithoutExt = dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
            String extension = dotIndex == -1 ? "jpg" : fileName.substring(dotIndex + 1);

            // Create the compressed file path
            String compressedFilePath = originalFile.getParent() + File.separator + nameWithoutExt + "_compressed." + extension;

            // Calculate original file size
            long originalSize = originalFile.length();

            // Compress the image and save it
            Thumbnails.of(originalFile)
                    .scale(1)
                    //.size(800, 600)      // Resize the image, adjust size as needed
                    .outputQuality(0.1) // Set compression quality (0.0 - 1.0)
                    .toFile(compressedFilePath);

            // Calculate compressed file size
            File compressedFile = new File(compressedFilePath);
            long compressedSize = compressedFile.length();

            // Calculate space saved
            long spaceSaved = originalSize - compressedSize;
            double percentageSaved = (spaceSaved / (double) originalSize) * 100;

            // Print the results
            System.out.printf("Compressed %s: saved %d bytes (%.2f%%)\n", imagePath, spaceSaved, percentageSaved);

            return compressedFilePath;
        } catch (IOException e) {
            System.err.println("Error compressing image: " + e.getMessage());
        }
        return null;
    }

    @Scheduled(cron = "0 55 16 * * *")
    public void compressOldImages() {

        System.out.println("RUNNING CRON IMAGE COMPRESSION");

        List<Post> allPosts = repository_post.findAll();
        LocalDateTime oneMonthAgo = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);

        for (Post post : allPosts) {
            String imageLocation = post.getPictureLocation();
            if (imageLocation != null && post.getCreatedDate().isBefore(oneMonthAgo)) {
                try {
                    Path imagePath = Path.of(imageLocation);
                    String compressedImagePath = compressImage(imageLocation);
                    Path compressedImage = Path.of(compressedImagePath);

                    // Replace the original file with the compressed one
                    Files.move(compressedImage, imagePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.err.printf("Failed to overwrite file: %s : %s\n", imageLocation, e.getMessage());
                }
            }
        }
    }
}
