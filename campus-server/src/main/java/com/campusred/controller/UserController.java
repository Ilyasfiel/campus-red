package com.campusred.controller;

import com.campusred.auth.CurrentUserId;
import com.campusred.auth.JwtUtil;
import com.campusred.dto.LoginRequest;
import com.campusred.dto.Result;
import com.campusred.dto.UserVO;
import com.campusred.entity.User;
import com.campusred.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest req) {
        User user = userService.login(req.getCode(), req.getNickname(), req.getAvatarUrl(), req.getCampus());
        String token = jwtUtil.generateToken(user.getId());
        return Result.ok(Map.of(
                "token", token,
                "userId", user.getId(),
                "nickname", user.getNickname(),
                "avatarUrl", user.getAvatarUrl()
        ));
    }

    @GetMapping("/profile/{userId}")
    public Result<UserVO> profile(@PathVariable Long userId,
                                   @CurrentUserId Long currentUserId) {
        UserVO vo = userService.getProfile(userId, currentUserId);
        if (vo == null) return Result.fail(404, "用户不存在");
        return Result.ok(vo);
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody Map<String, String> body,
                                    @CurrentUserId Long userId) {
        userService.updateProfile(userId,
                body.get("nickname"), body.get("avatarUrl"),
                body.get("bio"), body.get("campus"));
        return Result.ok();
    }

    @GetMapping("/schools")
    public Result<List<String>> schools() {
        return Result.ok(userService.getSchoolList());
    }
}
