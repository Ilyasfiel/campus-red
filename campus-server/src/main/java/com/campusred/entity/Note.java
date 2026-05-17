package com.campusred.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("note")
public class Note {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String images;
    private String tags;
    private String location;
    private String campus;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
