package com.campusred.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tree_hole")
public class TreeHole {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    private String tag;
    private Integer likeCount;
    private Integer commentCount;
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
