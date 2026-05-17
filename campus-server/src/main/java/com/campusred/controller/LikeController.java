package com.campusred.controller;

import com.campusred.auth.CurrentUserId;
import com.campusred.dto.Result;
import com.campusred.service.CollectService;
import com.campusred.service.LikeService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class LikeController {
    private final LikeService likeService;
    private final CollectService collectService;

    public LikeController(LikeService likeService, CollectService collectService) {
        this.likeService = likeService;
        this.collectService = collectService;
    }

    @PostMapping("/{id}/like")
    public Result<Map<String, Object>> toggleLike(@PathVariable Long id,
                                                   @CurrentUserId Long userId) {
        boolean liked = likeService.toggleLike(userId, id);
        return Result.ok(Map.of("liked", liked));
    }

    @PostMapping("/{id}/collect")
    public Result<Map<String, Object>> toggleCollect(@PathVariable Long id,
                                                      @CurrentUserId Long userId) {
        boolean collected = collectService.toggleCollect(userId, id);
        return Result.ok(Map.of("collected", collected));
    }
}
