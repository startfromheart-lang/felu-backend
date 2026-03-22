package com.aihoo.digital.dto;

import lombok.Data;

@Data
public class RecognizeQueryResponse {
    private String status;
    private Integer respDataStatus;
    private String error;
}
