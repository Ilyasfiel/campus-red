package com.campusred.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tree_hole_comment")
public class TreeHoleComment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long holeId;
    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
