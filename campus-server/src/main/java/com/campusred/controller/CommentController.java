package com.campusred.controller;

import com.campusred.auth.CurrentUserId;
import com.campusred.dto.CommentVO;
import com.campusred.dto.Result;
import com.campusred.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/note/{noteId}")
    public Result<List<CommentVO>> listByNote(@PathVariable Long noteId) {
        return Result.ok(commentService.getCommentsByNoteId(noteId));
    }

    @PostMapping
    public Result<CommentVO> add(@RequestBody Map<String, Object> body,
                                  @CurrentUserId Long userId) {
        Long noteId = Long.parseLong(body.get("noteId").toString());
        Long parentId = body.get("parentId") != null ? Long.parseLong(body.get("parentId").toString()) : null;
        String content = body.get("content").toString();
        return Result.ok(commentService.addComment(userId, noteId, parentId, content));
    }
}
