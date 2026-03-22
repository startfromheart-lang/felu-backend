package com.aihoo.digital.dto;

/**
 * 识别查询结果内部类
 */
public class RecognizeQueryResult {
    /**
     * 任务状态
     */
    private String status;

    /**
     * resp_data中的status
     */
    private Integer respDataStatus;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRespDataStatus() {
        return respDataStatus;
    }

    public void setRespDataStatus(Integer respDataStatus) {
        this.respDataStatus = respDataStatus;
    }
}
