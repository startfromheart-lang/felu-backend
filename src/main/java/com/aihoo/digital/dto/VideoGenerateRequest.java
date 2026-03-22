package com.aihoo.digital.dto;

import jakarta.validation.constraints.NotBlank;

public class VideoGenerateRequest {

    @NotBlank(message = "数字人名称不能为空")
    private String name;

    @NotBlank(message = "角色类型不能为空")
    private String role;

    @NotBlank(message = "音色类型不能为空")
    private String voiceType;

    @NotBlank(message = "台词内容不能为空")
    private String script;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(String voiceType) {
        this.voiceType = voiceType;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
