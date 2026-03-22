package com.aihoo.digital.dto;

import lombok.Data;

@Data
public class VideoGenerateResponse {
    private String id;
    private String status;
    private String videoUrl;
    private String error;
}
