package com.aihoo.digital.dto;

import jakarta.validation.constraints.NotBlank;

public class ComicGenerateRequest {

    @NotBlank(message = "漫画内容不能为空")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
