package com.aihoo.digital.dto;

import lombok.Data;

@Data
public class GenerateVideoRequest {
    private String imageUrl;
    private String audioUrl;
    private String voiceType;
}
