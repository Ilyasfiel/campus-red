package com.campusred.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${campus.upload.path:./uploads}")
    private String uploadPath;

    public String upload(MultipartFile file) throws IOException {
        Path dir = Paths.get(uploadPath);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + ext;
        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());

        return "/uploads/" + filename;
    }
}
