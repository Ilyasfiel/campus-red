package com.campusred.controller;

import com.campusred.dto.Result;
import com.campusred.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    private final FileService fileService;

    public UploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String url = fileService.upload(file);
        return Result.ok(Map.of("url", url));
    }
}
