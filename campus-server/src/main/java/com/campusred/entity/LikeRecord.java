package com.campusred.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("like_record")
public class LikeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long noteId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
