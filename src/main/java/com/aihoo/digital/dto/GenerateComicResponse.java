package com.aihoo.digital.dto;

import lombok.Data;

import java.util.List;

@Data
public class GenerateComicResponse {
    private String requestId;
    private List<String> imageUrls;
}
