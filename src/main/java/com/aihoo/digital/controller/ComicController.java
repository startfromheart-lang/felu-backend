package com.aihoo.digital.controller;

import com.aihoo.digital.common.Result;
import com.aihoo.digital.dto.ComicGenerateRequest;
import com.aihoo.digital.dto.ComicGenerateResponse;
import com.aihoo.digital.dto.ComicQueryResponse;
import com.aihoo.digital.dto.ComicSubmitResponse;
import com.aihoo.digital.service.ComicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "漫画生成管理", description = "漫画生成、查询等接口")
@RestController
@RequestMapping("/comic")
public class ComicController {

    private final ComicService comicService;

    public ComicController(ComicService comicService) {
        this.comicService = comicService;
    }

    @Operation(summary = "第一步：解析内容并生成请求ID")
    @PostMapping("/parse")
    public Result<ComicGenerateResponse> parseContent(@Valid @RequestBody ComicGenerateRequest request) {
        ComicGenerateResponse response = comicService.parseContent(request);

        if ("failed".equals(response.getStatus())) {
            return Result.error(response.getError());
        }

        return Result.success("内容解析成功", response);
    }

    @Operation(summary = "第二步：提交漫画图片生成任务")
    @PostMapping("/generate/{requestId}")
    public Result<ComicSubmitResponse> generateComicImages(@PathVariable String requestId) {
        ComicSubmitResponse response = comicService.generateComicImages(requestId);

        if ("failed".equals(response.getStatus())) {
            return Result.error(response.getError());
        }

        return Result.success("漫画图片生成任务已提交", response);
    }

    @Operation(summary = "第三步：获取生成的漫画图片")
    @GetMapping("/images/{requestId}")
    public Result<ComicQueryResponse> getComicImages(@PathVariable String requestId) {
        ComicQueryResponse response = comicService.getComicImages(requestId);

        if ("failed".equals(response.getStatus())) {
            return Result.error(response.getError());
        }

        return Result.success(response);
    }
}
