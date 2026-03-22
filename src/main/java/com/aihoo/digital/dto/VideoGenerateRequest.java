package com.aihoo.digital.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VideoGenerateRequest {

    @NotBlank(message = "数字人名称不能为空")
    private String name;

    @NotBlank(message = "角色类型不能为空")
    private String role;

    @NotBlank(message = "音色类型不能为空")
    private String voiceType;

    @NotBlank(message = "台词内容不能为空")
    private String script;
}
