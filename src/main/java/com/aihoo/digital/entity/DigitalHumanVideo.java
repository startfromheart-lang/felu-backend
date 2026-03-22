package com.aihoo.digital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("digital_human_video")
public class DigitalHumanVideo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    @TableField("video_name")
    private String videoName;

    @TableField("digital_human_name")
    private String digitalHumanName;

    @TableField("digital_human_code")
    private String digitalHumanCode;

    private String role;

    @TableField("voice_type")
    private String voiceType;

    @TableField("voice_code")
    private String voiceCode;

    private String script;

    private String status;

    @TableField("video_url")
    private String videoUrl;

    @TableField("video_duration")
    private Integer videoDuration;

    @TableField("video_size")
    private Long videoSize;

    @TableField("thumbnail_url")
    private String thumbnailUrl;

    @TableField("error_message")
    private String errorMessage;

    @TableField("retry_count")
    private Integer retryCount;

    private String provider;

    @TableField("provider_task_id")
    private String providerTaskId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
