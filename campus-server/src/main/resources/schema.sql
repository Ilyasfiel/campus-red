-- CampusRed 校园小红书 建表SQL
CREATE DATABASE IF NOT EXISTS campus_red DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_red;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    openid VARCHAR(64) NOT NULL COMMENT '微信openid',
    nickname VARCHAR(64) DEFAULT '' COMMENT '昵称',
    avatar_url VARCHAR(512) DEFAULT '' COMMENT '头像URL',
    bio VARCHAR(256) DEFAULT '' COMMENT '个人简介',
    campus VARCHAR(128) DEFAULT '' COMMENT '所在学校',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 笔记表
CREATE TABLE IF NOT EXISTS `note` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '发布用户ID',
    title VARCHAR(200) DEFAULT '' COMMENT '标题',
    content TEXT COMMENT '正文内容',
    images JSON COMMENT '图片URL列表',
    tags JSON COMMENT '标签列表',
    location VARCHAR(200) DEFAULT '' COMMENT '发布位置',
    view_count INT DEFAULT 0 COMMENT '浏览数',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    collect_count INT DEFAULT 0 COMMENT '收藏数',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-正常 1-删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记表';

-- 笔记图片表
CREATE TABLE IF NOT EXISTS `note_image` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    note_id BIGINT NOT NULL COMMENT '所属笔记ID',
    url VARCHAR(512) NOT NULL COMMENT '图片URL',
    sort_order INT DEFAULT 0 COMMENT '排序',
    KEY idx_note_id (note_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记图片表';

-- 点赞表
CREATE TABLE IF NOT EXISTS `like_record` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    note_id BIGINT NOT NULL COMMENT '笔记ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_note (user_id, note_id),
    KEY idx_note_id (note_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '评论用户ID',
    note_id BIGINT NOT NULL COMMENT '所属笔记ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论ID，0表示一级评论',
    content VARCHAR(1000) NOT NULL COMMENT '评论内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_note_id (note_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 树洞表
CREATE TABLE IF NOT EXISTS `tree_hole` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT COMMENT '内容',
    tag VARCHAR(64) DEFAULT '' COMMENT '心情标签',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-正常 1-删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='树洞表';

-- 树洞评论表
CREATE TABLE IF NOT EXISTS `tree_hole_comment` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hole_id BIGINT NOT NULL COMMENT '所属树洞ID',
    content VARCHAR(1000) NOT NULL COMMENT '评论内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_hole_id (hole_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='树洞评论表';

-- 浏览记录表
CREATE TABLE IF NOT EXISTS `view_record` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    note_id BIGINT NOT NULL COMMENT '笔记ID',
    view_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    KEY idx_user_id (user_id),
    KEY idx_note_id (note_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览记录表';

-- 关注表
CREATE TABLE IF NOT EXISTS `follow` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id BIGINT NOT NULL COMMENT '关注者ID',
    followee_id BIGINT NOT NULL COMMENT '被关注者ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_follower_followee (follower_id, followee_id),
    KEY idx_followee_id (followee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注关系表';
