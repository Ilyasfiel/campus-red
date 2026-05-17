package com.campusred.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campusred.auth.CurrentUserId;
import com.campusred.dto.Result;
import com.campusred.entity.TreeHole;
import com.campusred.entity.TreeHoleComment;
import com.campusred.service.TreeHoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/treehole")
public class TreeHoleController {
    private final TreeHoleService treeHoleService;

    public TreeHoleController(TreeHoleService treeHoleService) {
        this.treeHoleService = treeHoleService;
    }

    @GetMapping
    public Result<Page<TreeHole>> list(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return Result.ok(treeHoleService.getList(page, size));
    }

    @PostMapping
    public Result<TreeHole> create(@RequestBody Map<String, String> body) {
        return Result.ok(treeHoleService.create(body.get("content"),
                body.getOrDefault("tag", "")));
    }

    @PostMapping("/{id}/like")
    public Result<Map<String, Object>> like(@PathVariable Long id,
                                             @CurrentUserId Long userId) {
        return Result.ok(treeHoleService.toggleLike(id, userId));
    }

    @GetMapping("/{id}/comments")
    public Result<List<TreeHoleComment>> comments(@PathVariable Long id) {
        return Result.ok(treeHoleService.getComments(id));
    }

    @PostMapping("/{id}/comments")
    public Result<TreeHoleComment> addComment(@PathVariable Long id,
                                               @RequestBody Map<String, String> body) {
        return Result.ok(treeHoleService.addComment(id, body.get("content")));
    }
}
