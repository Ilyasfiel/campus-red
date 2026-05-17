package com.campusred.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "code不能为空")
    private String code;

    private String nickname;
    private String avatarUrl;
    private String campus;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }
}
