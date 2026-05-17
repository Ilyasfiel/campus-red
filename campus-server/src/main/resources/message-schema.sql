USE campus_red;

-- 私信表
CREATE TABLE IF NOT EXISTS `message` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_user_id BIGINT NOT NULL COMMENT '发送者ID',
    to_user_id BIGINT NOT NULL COMMENT '接收者ID',
    content VARCHAR(2000) NOT NULL COMMENT '消息内容',
    is_read TINYINT DEFAULT 0 COMMENT '0未读 1已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_from_to (from_user_id, to_user_id),
    KEY idx_to_user (to_user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私信表';

-- 树洞点赞记录表
CREATE TABLE IF NOT EXISTS `tree_hole_like` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    hole_id BIGINT NOT NULL COMMENT '树洞ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_hole (user_id, hole_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='树洞点赞记录表';

-- 收藏记录表
CREATE TABLE IF NOT EXISTS `collect_record` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    note_id BIGINT NOT NULL COMMENT '笔记ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_note (user_id, note_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏记录表';

-- 系统通知表
CREATE TABLE IF NOT EXISTS `notification` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    type VARCHAR(32) NOT NULL COMMENT '通知类型: like/comment/follow/system',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content VARCHAR(1000) DEFAULT '' COMMENT '通知内容',
    related_id BIGINT DEFAULT 0 COMMENT '关联ID(笔记/评论等)',
    is_read TINYINT DEFAULT 0 COMMENT '0未读 1已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_user_read (user_id, is_read),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';
