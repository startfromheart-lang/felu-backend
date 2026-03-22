-- ====================================================
-- AIHOO数字人视频生成平台 - 数据库初始化脚本
-- ====================================================

-- 创建数据库
DROP DATABASE IF EXISTS aihoo_digital;
CREATE DATABASE aihoo_digital DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aihoo_digital;

-- ====================================================
-- 1. 用户表
-- ====================================================
CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(500) COMMENT '头像URL',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ====================================================
-- 2. 角色表
-- ====================================================
CREATE TABLE IF NOT EXISTS `role` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ====================================================
-- 3. 用户角色关联表
-- ====================================================
CREATE TABLE IF NOT EXISTS `user_role` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ====================================================
-- 4. 数字人配置表
-- ====================================================
CREATE TABLE IF NOT EXISTS `digital_human_config` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '数字人名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '数字人编码',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    description VARCHAR(500) COMMENT '描述',
    category VARCHAR(50) COMMENT '分类',
    preview_url VARCHAR(500) COMMENT '预览视频URL',
    provider VARCHAR(50) NOT NULL COMMENT '提供商：jimeng/aliyun/tencent',
    provider_id VARCHAR(100) COMMENT '提供商ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    INDEX idx_code (code),
    INDEX idx_category (category),
    INDEX idx_provider (provider)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数字人配置表';

-- ====================================================
-- 5. 音色配置表
-- ====================================================
CREATE TABLE IF NOT EXISTS `voice_config` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '音色名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '音色编码',
    gender VARCHAR(10) NOT NULL COMMENT '性别：male/female/child',
    description VARCHAR(500) COMMENT '描述',
    sample_url VARCHAR(500) COMMENT '示例音频URL',
    provider VARCHAR(50) NOT NULL COMMENT '提供商：jimeng/aliyun/tencent',
    provider_id VARCHAR(100) COMMENT '提供商ID',
    language VARCHAR(20) DEFAULT 'zh-CN' COMMENT '语言',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    INDEX idx_code (code),
    INDEX idx_gender (gender),
    INDEX idx_provider (provider)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音色配置表';

-- ====================================================
-- 6. 数字人视频表
-- ====================================================
CREATE TABLE IF NOT EXISTS `digital_human_video` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    video_name VARCHAR(200) NOT NULL COMMENT '视频名称',
    digital_human_name VARCHAR(100) NOT NULL COMMENT '数字人名称',
    digital_human_code VARCHAR(50) NOT NULL COMMENT '数字人编码',
    role VARCHAR(50) NOT NULL COMMENT '角色类型',
    voice_type VARCHAR(50) NOT NULL COMMENT '音色类型',
    voice_code VARCHAR(50) NOT NULL COMMENT '音色编码',
    script TEXT NOT NULL COMMENT '台词内容',
    status VARCHAR(20) NOT NULL DEFAULT 'processing' COMMENT '状态：processing/completed/failed',
    video_url VARCHAR(500) COMMENT '视频URL',
    video_duration INT COMMENT '视频时长（秒）',
    video_size BIGINT COMMENT '视频大小（字节）',
    thumbnail_url VARCHAR(500) COMMENT '缩略图URL',
    error_message TEXT COMMENT '错误信息',
    retry_count INT NOT NULL DEFAULT 0 COMMENT '重试次数',
    provider VARCHAR(50) COMMENT '提供商',
    provider_task_id VARCHAR(100) COMMENT '提供商任务ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_provider_task_id (provider_task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数字人视频表';

-- ====================================================
-- 7. 视频模板表
-- ====================================================
CREATE TABLE IF NOT EXISTS `video_template` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    template_code VARCHAR(50) NOT NULL UNIQUE COMMENT '模板编码',
    description VARCHAR(500) COMMENT '描述',
    thumbnail_url VARCHAR(500) COMMENT '缩略图URL',
    preview_url VARCHAR(500) COMMENT '预览URL',
    background_url VARCHAR(500) COMMENT '背景URL',
    category VARCHAR(50) COMMENT '分类',
    is_system TINYINT NOT NULL DEFAULT 0 COMMENT '是否系统模板：0-否 1-是',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    INDEX idx_template_code (template_code),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频模板表';

-- ====================================================
-- 初始化数据
-- ====================================================

-- 初始化角色数据
INSERT INTO `role` (role_name, role_code, description) VALUES
('超级管理员', 'ADMIN', '系统超级管理员'),
('普通用户', 'USER', '系统普通用户');

-- 初始化用户数据（密码：admin123）
INSERT INTO `user` (username, password, nickname, email, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin@aihoo.com', 1),
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户', 'user@aihoo.com', 1);

-- 初始化用户角色关联
INSERT INTO `user_role` (user_id, role_id) VALUES
(1, 1),
(2, 2);

-- 初始化数字人配置数据
INSERT INTO `digital_human_config` (name, code, description, category, provider, provider_id, status, sort_order) VALUES
('李主播', 'anchor_li', '专业新闻主播形象', '新闻主播', 'jimeng', 'jimeng_001', 1, 1),
('王老师', 'teacher_wang', '教育培训专业讲师', '教育培训', 'jimeng', 'jimeng_002', 1, 2),
('小助手', 'assistant_xiao', '智能AI助手形象', 'AI助手', 'jimeng', 'jimeng_003', 1, 3),
('记者小张', 'reporter_zhang', '外景记者形象', '记者', 'jimeng', 'jimeng_004', 1, 4),
('商务经理', 'manager_biz', '商务会议主持', '商务', 'jimeng', 'jimeng_005', 1, 5);

-- 初始化音色配置数据
INSERT INTO `voice_config` (name, code, gender, description, provider, provider_id, language, status, sort_order) VALUES
('女声-温柔', 'female_gentle', 'female', '温柔知性的女声', 'jimeng', 'voice_female_001', 'zh-CN', 1, 1),
('女声-活泼', 'female_lively', 'female', '活泼开朗的女声', 'jimeng', 'voice_female_002', 'zh-CN', 1, 2),
('男声-磁性', 'male_magnetic', 'male', '磁性好听的男声', 'jimeng', 'voice_male_001', 'zh-CN', 1, 3),
('男声-沉稳', 'male_steady', 'male', '沉稳专业的男声', 'jimeng', 'voice_male_002', 'zh-CN', 1, 4),
('童声', 'child_cute', 'child', '可爱的童声', 'jimeng', 'voice_child_001', 'zh-CN', 1, 5),
('女声-英语', 'female_english', 'female', '英语发音女声', 'jimeng', 'voice_female_003', 'en-US', 1, 6);

-- 初始化视频模板数据
INSERT INTO `video_template` (template_name, template_code, description, category, is_system, status, sort_order) VALUES
('简约白底', 'simple_white', '白色简约背景模板', '简约', 1, 1, 1),
('科技蓝调', 'tech_blue', '科技感蓝色背景模板', '科技', 1, 1, 2),
('商务会议', 'business', '商务会议场景模板', '商务', 1, 1, 3),
('教育培训', 'education', '教育培训场景模板', '教育', 1, 1, 4),
('新闻播报', 'news', '新闻播报场景模板', '新闻', 1, 1, 5);

-- ====================================================
-- 创建视图
-- ====================================================

-- 视频统计视图
CREATE OR REPLACE VIEW `video_statistics` AS
SELECT
    DATE(create_time) as stat_date,
    COUNT(*) as total_count,
    SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completed_count,
    SUM(CASE WHEN status = 'processing' THEN 1 ELSE 0 END) as processing_count,
    SUM(CASE WHEN status = 'failed' THEN 1 ELSE 0 END) as failed_count
FROM `digital_human_video`
WHERE deleted = 0
GROUP BY DATE(create_time);

-- ====================================================
-- 存储过程
-- ====================================================

DELIMITER //

-- 清理失败的视频任务
CREATE PROCEDURE `clean_failed_videos`(IN days INT)
BEGIN
    UPDATE `digital_human_video`
    SET deleted = 1
    WHERE status = 'failed'
      AND deleted = 0
      AND create_time < DATE_SUB(NOW(), INTERVAL days DAY);
END //

DELIMITER ;

-- ====================================================
-- 初始化完成
-- ====================================================
