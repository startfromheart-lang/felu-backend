package com.aihoo.digital.service;

import com.aihoo.digital.dto.*;
import com.aihoo.digital.entity.DigitalHumanVideo;
/**
 * 数字人视频服务接口
 * 这里可以整理成模板方法
 */
public interface DigitalHumanVideoService {

    /**
     * 执行生成视频
     * @param request 视频生成请求参数
     * @return 视频生成响应结果
     */
    VideoGenerateResponse doGenerateVideo(VideoGenerateRequest request);

    /**
     * 获取视频详情
     * @param id 视频ID
     * @return 数字人视频实体
     */
    DigitalHumanVideo getVideoDetail(String id);

    /**
     * 获取视频列表
     * @param page 页码
     * @param pageSize 每页大小
     * @return 数字人视频列表
     */
    java.util.List<DigitalHumanVideo> getVideoList(Integer page, Integer pageSize);

    /**
     * 对象识别
     * @param request 对象识别请求参数
     * @return 对象识别响应结果
     */
    ObjectRecognizeResponse objectRecognize(ObjectRecognizeRequest request);

    /**
     * 获取识别结果
     * @param taskId 任务ID
     * @return 识别查询响应结果
     */
    RecognizeQueryResponse getRecognizeResult(String taskId);

    /**
     * 对象检测
     * @param request 对象检测请求参数
     * @return 对象检测响应结果
     */
    ObjectDetectResponse objectDetect(ObjectDetectRequest request);

    /**
     * 生成视频
     * @param request 生成视频请求参数
     * @return 生成视频响应结果
     */
    GenerateVideoResponse generateVideo(GenerateVideoRequest request);

    /**
     * 获取视频URL
     * @param taskId 任务ID
     * @return 视频查询响应结果
     */
    VideoQueryResponse getVideoUrl(String taskId);
}
