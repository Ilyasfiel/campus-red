package com.campusred.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tree_hole_like")
public class TreeHoleLike {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long holeId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
