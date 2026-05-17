package com.campusred.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("view_record")
public class ViewRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long noteId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime viewTime;
}
