package com.aihoo.digital.dto;

public class RecognizeQueryResponse {
    private String status;
    private Integer respDataStatus;
    private String error;

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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
