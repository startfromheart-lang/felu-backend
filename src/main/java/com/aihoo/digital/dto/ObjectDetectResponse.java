package com.aihoo.digital.dto;

import lombok.Data;

import java.util.List;

@Data
public class ObjectDetectResponse {
    private String taskId;
    private String status;
    private List<String> imageUrls;
    private String error;
}
