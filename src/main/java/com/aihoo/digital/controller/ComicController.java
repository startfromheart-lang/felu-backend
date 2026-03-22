package com.aihoo.digital.controller;

import com.aihoo.digital.dto.GenerateComicRequest;
import com.aihoo.digital.dto.GenerateComicResponse;
import com.aihoo.digital.service.ComicService;
import com.aihoo.digital.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/comic")
@RequiredArgsConstructor
public class ComicController {

    private final ComicService comicService;

    /**
     * 第一步：接收用户输入，生成请求ID并启动异步任务
     */
    @PostMapping("/generate")
    public Result<String> generateComic(@RequestBody GenerateComicRequest request) {
        try {
            String requestId = comicService.generateComicTask(request);
            log.info("Comic generation task started with requestId: {}", requestId);
            return Result.success(requestId);
        } catch (Exception e) {
            log.error("Error in generateComic", e);
            return Result.error("Failed to start comic generation task");
        }
    }

    /**
     * 第三步：根据请求ID返回生成的图片URL
     */
    @GetMapping("/result/{requestId}")
    public Result<GenerateComicResponse> getComicResult(@PathVariable String requestId) {
        try {
            GenerateComicResponse response = comicService.getComicResult(requestId);
            log.info("Retrieved comic result for requestId: {}", requestId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("Error in getComicResult", e);
            return Result.error("Failed to get comic result");
        }
    }

    /**
     * 用于提供图片访问的接口
     */
    @GetMapping("/images/**")
    public void serveImage() {
        // 实际部署时，应该通过Nginx等静态资源服务器提供图片访问
        // 这里仅作为占位符
    }
}
