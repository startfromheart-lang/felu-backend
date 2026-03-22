package com.aihoo.digital.controller;

import com.aihoo.digital.common.Result;
import com.aihoo.digital.dto.*;
import com.aihoo.digital.entity.DigitalHumanVideo;
import com.aihoo.digital.service.DigitalHumanVideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "数字人视频管理", description = "数字人视频生成、查询等接口")
@RestController
@RequestMapping("/digital-human")
@RequiredArgsConstructor
public class DigitalHumanController {

    private final DigitalHumanVideoService videoService;

    @Operation(summary = "生成数字人视频")
    @PostMapping("/generate")
    public Result<VideoGenerateResponse> generateVideo(@Valid @RequestBody VideoGenerateRequest request) {
        VideoGenerateResponse response = videoService.doGenerateVideo(request);
        return Result.success("视频生成请求已提交", response);
    }

    @Operation(summary = "对象识别")
    @PostMapping("/object-recognize")
    public Result<ObjectRecognizeResponse> objectRecognize(@Valid @RequestBody ObjectRecognizeRequest request) {
        ObjectRecognizeResponse response = videoService.objectRecognize(request);
        return Result.success("对象识别请求已提交", response);
    }

    @Operation(summary = "获取识别结果")
    @GetMapping("/recognize-result/{taskId}")
    public Result<RecognizeQueryResponse> getRecognizeResult(@PathVariable String taskId) {
        RecognizeQueryResponse response = videoService.getRecognizeResult(taskId);
        return Result.success(response);
    }

    @Operation(summary = "对象检测")
    @PostMapping("/object-detect")
    public Result<ObjectDetectResponse> objectDetect(@Valid @RequestBody ObjectDetectRequest request) {
        ObjectDetectResponse response = videoService.objectDetect(request);
        return Result.success("对象检测请求已提交", response);
    }

    @Operation(summary = "生成视频")
    @PostMapping("/generate-video")
    public Result<GenerateVideoResponse> generateVideo(@Valid @RequestBody GenerateVideoRequest request) {
        GenerateVideoResponse response = videoService.generateVideo(request);
        return Result.success("视频生成请求已提交", response);
    }

    @Operation(summary = "获取视频URL")
    @GetMapping("/video-url/{taskId}")
    public Result<VideoQueryResponse> getVideoUrl(@PathVariable String taskId) {
        VideoQueryResponse response = videoService.getVideoUrl(taskId);
        return Result.success(response);
    }

    @Operation(summary = "获取视频详情")
    @GetMapping("/detail/{id}")
    public Result<DigitalHumanVideo> getVideoDetail(@PathVariable String id) {
        DigitalHumanVideo video = videoService.getVideoDetail(id);
        return Result.success(video);
    }

    @Operation(summary = "获取视频列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getVideoList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        List<DigitalHumanVideo> list = videoService.getVideoList(page, pageSize);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());

        return Result.success(result);
    }
}
