package com.campusred.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campusred.auth.CurrentUserId;
import com.campusred.dto.NoteRequest;
import com.campusred.dto.NoteVO;
import com.campusred.dto.Result;
import com.campusred.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public Result<Page<NoteVO>> list(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @CurrentUserId Long userId,
                                      @RequestParam(required = false) String campus,
                                      @RequestParam(required = false, defaultValue = "latest") String sort,
                                      @RequestParam(required = false) String tag,
                                      @RequestParam(required = false) String keyword) {
        return Result.ok(noteService.getNoteList(page, size, userId, campus, sort, tag, keyword));
    }

    @PostMapping
    public Result<NoteVO> create(@Valid @RequestBody NoteRequest req,
                                  @CurrentUserId Long userId) {
        NoteVO vo = noteService.createNote(userId, req.getTitle(), req.getContent(),
                req.getImages(), req.getTags(), req.getLocation());
        return Result.ok(vo);
    }

    @GetMapping("/{id}")
    public Result<NoteVO> detail(@PathVariable Long id,
                                  @CurrentUserId Long userId) {
        NoteVO vo = noteService.getNoteDetail(id, userId);
        if (vo == null) return Result.fail(404, "笔记不存在");
        return Result.ok(vo);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, @CurrentUserId Long userId) {
        boolean deleted = noteService.deleteNote(id, userId);
        if (!deleted) return Result.fail(403, "无权删除或笔记不存在");
        return Result.ok();
    }

    @GetMapping("/user/{userId}")
    public Result<Page<NoteVO>> userNotes(@PathVariable Long userId,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @CurrentUserId Long currentUserId) {
        return Result.ok(noteService.getUserNotes(userId, page, size, currentUserId));
    }
}
