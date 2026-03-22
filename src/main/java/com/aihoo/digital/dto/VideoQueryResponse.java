package com.aihoo.digital.dto;

import lombok.Data;

@Data
public class VideoQueryResponse {
    private String status;
    private String videoUrl;
    private String error;
}
