package com.campusred.dto;

public class UserVO {
    private Long id;
    private String nickname;
    private String avatarUrl;
    private String bio;
    private String campus;
    private Integer noteCount;
    private Integer followerCount;
    private Integer followingCount;
    private Boolean isFollowed;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }
    public Integer getNoteCount() { return noteCount; }
    public void setNoteCount(Integer noteCount) { this.noteCount = noteCount; }
    public Integer getFollowerCount() { return followerCount; }
    public void setFollowerCount(Integer followerCount) { this.followerCount = followerCount; }
    public Integer getFollowingCount() { return followingCount; }
    public void setFollowingCount(Integer followingCount) { this.followingCount = followingCount; }
    public Boolean getIsFollowed() { return isFollowed; }
    public void setIsFollowed(Boolean isFollowed) { this.isFollowed = isFollowed; }
}
