package com.campusred.dto;

import java.time.LocalDateTime;
import java.util.List;

public class NoteVO {
    private Long id;
    private String title;
    private String content;
    private List<String> images;
    private List<String> tags;
    private String location;
    private String campus;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private Boolean isLiked;
    private Boolean isCollected;
    private LocalDateTime createTime;
    private UserVO user;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    public Integer getCollectCount() { return collectCount; }
    public void setCollectCount(Integer collectCount) { this.collectCount = collectCount; }
    public Boolean getIsLiked() { return isLiked; }
    public void setIsLiked(Boolean isLiked) { this.isLiked = isLiked; }
    public Boolean getIsCollected() { return isCollected; }
    public void setIsCollected(Boolean isCollected) { this.isCollected = isCollected; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public UserVO getUser() { return user; }
    public void setUser(UserVO user) { this.user = user; }
}
