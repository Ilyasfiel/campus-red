package com.campusred.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class NoteRequest {
    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;
    private List<String> images;
    private List<String> tags;
    private String location;

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
}
