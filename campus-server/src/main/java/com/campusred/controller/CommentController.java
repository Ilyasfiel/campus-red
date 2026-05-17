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
        Object noteIdObj = body.get("noteId");
        Object contentObj = body.get("content");
        if (noteIdObj == null || contentObj == null) {
            return Result.fail(400, "参数不完整");
        }
        String content = contentObj.toString().trim();
        if (content.isEmpty()) return Result.fail(400, "评论内容不能为空");
        if (content.length() > 1000) return Result.fail(400, "评论内容过长");

        Long noteId = Long.parseLong(noteIdObj.toString());
        Long parentId = null;
        if (body.get("parentId") != null) {
            parentId = Long.parseLong(body.get("parentId").toString());
        }
        return Result.ok(commentService.addComment(userId, noteId, parentId, content));
    }
}
