package com.campusred.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CommentVO {
    private Long id;
    private Long noteId;
    private Long parentId;
    private String content;
    private LocalDateTime createTime;
    private UserVO user;
    private List<CommentVO> replies;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getNoteId() { return noteId; }
    public void setNoteId(Long noteId) { this.noteId = noteId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public UserVO getUser() { return user; }
    public void setUser(UserVO user) { this.user = user; }
    public List<CommentVO> getReplies() { return replies; }
    public void setReplies(List<CommentVO> replies) { this.replies = replies; }
}
