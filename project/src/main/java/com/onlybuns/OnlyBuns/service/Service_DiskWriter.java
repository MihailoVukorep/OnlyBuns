package com.onlybuns.OnlyBuns.service;

import org.hibernate.sql.results.internal.TupleImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class Service_DiskWriter {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveImage(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) { Files.createDirectories(uploadPath); }

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

//    public String saveImage(MultipartFile file) {
//        try {
//            Path uploadPath = Paths.get(uploadDir);
//            if (!Files.exists(uploadPath)) { Files.createDirectories(uploadPath); }
//
//            Path filePath = uploadPath.resolve(file.getOriginalFilename());
//            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//            System.out.println("saveImage: " + filePath);
//            return filePath.toString();
//        }
//        catch (Exception ignored) { }
//        return null;
//    }

    public void deleteImage(String path) {
        try {
            System.out.println("deleteImage: " + path);
            Files.delete(Paths.get(path));
        }
        catch (Exception e) {
            System.out.println("deleteImage fail: " + e.getMessage());

        }
    }
}
