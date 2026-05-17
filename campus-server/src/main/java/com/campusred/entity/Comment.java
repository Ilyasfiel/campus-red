package com.campusred.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long noteId;
    private Long parentId;
    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private java.util.List<Comment> replies;
}
