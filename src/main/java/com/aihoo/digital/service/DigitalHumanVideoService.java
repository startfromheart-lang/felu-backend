package com.aihoo.digital.service;

import com.aihoo.digital.dto.*;
import com.aihoo.digital.entity.DigitalHumanVideo;
/**
 * 
 * 这里可以整理成模板方法
 */
public interface DigitalHumanVideoService {

    VideoGenerateResponse doGenerateVideo(VideoGenerateRequest request);

    DigitalHumanVideo getVideoDetail(String id);

    java.util.List<DigitalHumanVideo> getVideoList(Integer page, Integer pageSize);

    ObjectRecognizeResponse objectRecognize(ObjectRecognizeRequest request);

    RecognizeQueryResponse getRecognizeResult(String taskId);

    ObjectDetectResponse objectDetect(ObjectDetectRequest request);

    GenerateVideoResponse generateVideo(GenerateVideoRequest request);

    VideoQueryResponse getVideoUrl(String taskId);
}
