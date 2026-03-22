package com.aihoo.digital.dto;

import lombok.Data;

/**
 * 识别查询结果内部类
 */
@Data
public class RecognizeQueryResult {
    /**
     * 任务状态
     */
    private String status;

    /**
     * resp_data中的status
     */
    private Integer respDataStatus;
}
