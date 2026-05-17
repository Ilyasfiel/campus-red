package com.campusred.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("note_image")
public class NoteImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long noteId;
    private String url;
    private Integer sortOrder;
}
