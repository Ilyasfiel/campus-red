package com.campusred.controller;

import com.campusred.auth.CurrentUserId;
import com.campusred.dto.Result;
import com.campusred.service.FollowService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class FollowController {
    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{id}/follow")
    public Result<Map<String, Object>> toggleFollow(@PathVariable Long id,
                                                     @CurrentUserId Long followerId) {
        boolean following = followService.toggleFollow(followerId, id);
        return Result.ok(Map.of("following", following));
    }

    @GetMapping("/{id}/followers")
    public Result<?> followers(@PathVariable Long id) {
        return Result.ok(followService.getFollowers(id));
    }

    @GetMapping("/{id}/following")
    public Result<?> following(@PathVariable Long id) {
        return Result.ok(followService.getFollowing(id));
    }
}
