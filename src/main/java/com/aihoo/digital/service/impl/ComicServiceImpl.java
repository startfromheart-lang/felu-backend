package com.aihoo.digital.service.impl;

import com.aihoo.digital.dto.ComicGenerateRequest;
import com.aihoo.digital.dto.ComicGenerateResponse;
import com.aihoo.digital.dto.ComicQueryResponse;
import com.aihoo.digital.dto.ComicSubmitResponse;
import com.aihoo.digital.service.ComicService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.impl.VisualServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ComicServiceImpl implements ComicService {

    private static final Logger log = LoggerFactory.getLogger(ComicServiceImpl.class);

    @Value("${comic.image.base-path:/data/comic-images}")
    private String imageBasePath;

    @Value("${comic.image.base-url:http://localhost:8080/comic-images}")
    private String imageBaseUrl;

    // 内存存储请求ID与分段内容的映射
    private final ConcurrentHashMap<String, List<String>> requestContentMap = new ConcurrentHashMap<>();
    // 内存存储请求ID与任务ID列表的映射
    private final ConcurrentHashMap<String, List<String>> requestTaskMap = new ConcurrentHashMap<>();
    // 内存存储请求ID与生成图片路径的映射
    private final ConcurrentHashMap<String, List<String>> requestImageMap = new ConcurrentHashMap<>();

    @Override
    public ComicGenerateResponse parseContent(ComicGenerateRequest request) {
        ComicGenerateResponse response = new ComicGenerateResponse();

        try {
            String content = request.getContent();
            if (content == null || content.trim().isEmpty()) {
                response.setStatus("failed");
                response.setError("内容不能为空");
                return response;
            }

            // 生成请求唯一ID
            String requestId = UUID.randomUUID().toString().replace("-", "");

            // 将内容分成三段，使用 ||| 分隔
            List<String> segments = splitContentIntoSegments(content);

            // 存储到内存映射中
            requestContentMap.put(requestId, segments);

            response.setRequestId(requestId);
            response.setSegments(segments);
            response.setStatus("success");

            log.info("内容解析成功，requestId: {}, 分段数: {}", requestId, segments.size());
            return response;

        } catch (Exception e) {
            log.error("内容解析失败", e);
            response.setStatus("failed");
            response.setError(e.getMessage());
            return response;
        }
    }

    /**
     * 将内容分成三段，使用 ||| 分隔
     */
    private List<String> splitContentIntoSegments(String content) {
        List<String> segments = new ArrayList<>();

        // 如果内容中包含 |||，则按此分隔
        if (content.contains("|||")) {
            String[] parts = content.split("\\|\\|\\|");
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    segments.add(trimmed);
                }
            }
        } else {
            // 如果没有分隔符，尝试按段落或句子智能分段
            segments = smartSplitContent(content);
        }

        // 确保至少有一段
        if (segments.isEmpty()) {
            segments.add(content.trim());
        }

        return segments;
    }

    /**
     * 智能分段：将内容分成三段
     */
    private List<String> smartSplitContent(String content) {
        List<String> segments = new ArrayList<>();
        String trimmed = content.trim();

        // 按句子分隔（使用中文句号、英文句号、问号、感叹号）
        String[] sentences = trimmed.split("(?<=[。！？.!?])");

        if (sentences.length >= 3) {
            // 如果有3个或以上句子，平均分成三段
            int segmentSize = sentences.length / 3;
            int remainder = sentences.length % 3;

            int startIndex = 0;
            for (int i = 0; i < 3; i++) {
                int endIndex = startIndex + segmentSize + (i < remainder ? 1 : 0);
                StringBuilder segment = new StringBuilder();
                for (int j = startIndex; j < endIndex && j < sentences.length; j++) {
                    segment.append(sentences[j]);
                }
                segments.add(segment.toString().trim());
                startIndex = endIndex;
            }
        } else if (sentences.length == 2) {
            // 如果只有2个句子，第一段一个句子，后两段各一个句子（第二段可能较短）
            segments.add(sentences[0].trim());
            segments.add(sentences[1].trim());
            segments.add("续：" + sentences[1].trim());
        } else {
            // 如果只有1个句子，直接分成三段（按字符数大致均分）
            int length = trimmed.length();
            int segmentSize = length / 3;

            for (int i = 0; i < 3; i++) {
                int start = i * segmentSize;
                int end = (i == 2) ? length : (i + 1) * segmentSize;
                segments.add(trimmed.substring(start, end).trim());
            }
        }

        return segments;
    }

    @Override
    public ComicSubmitResponse generateComicImages(String requestId) {
        ComicSubmitResponse response = new ComicSubmitResponse();

        try {
            // 获取分段内容
            List<String> segments = requestContentMap.get(requestId);
            if (segments == null || segments.isEmpty()) {
                response.setStatus("failed");
                response.setError("未找到对应的内容，requestId: " + requestId);
                return response;
            }

            // 限制为3段
            int segmentCount = Math.min(segments.size(), 3);
            List<String> taskIds = new ArrayList<>();

            IVisualService visualService = VisualServiceImpl.getInstance();

            // 为每一段内容生成一张图片
            for (int i = 0; i < segmentCount; i++) {
                String segment = segments.get(i);

                JSONObject req = new JSONObject();
                req.put("req_key", "jimeng_t2i_v30");
                req.put("prompt", segment);

                Object apiResponse = visualService.cvSubmitTask(req);
                log.info("cvSubmitTask response: {}", JSON.toJSONString(apiResponse));

                JSONObject jsonResponse = JSON.parseObject(JSON.toJSONString(apiResponse));
                Integer code = jsonResponse.getInteger("code");

                if (code != null && code == 10000) {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    if (data != null) {
                        String taskId = data.getString("task_id");
                        taskIds.add(taskId);
                        log.info("第 {} 张图片生成任务提交成功，taskId: {}", i + 1, taskId);
                    }
                } else {
                    log.error("第 {} 张图片生成任务提交失败，code: {}, message: {}",
                            i + 1, code, jsonResponse.getString("message"));
                }
            }

            if (taskIds.isEmpty()) {
                response.setStatus("failed");
                response.setError("所有图片生成任务提交失败");
                return response;
            }

            // 存储任务ID列表
            requestTaskMap.put(requestId, taskIds);

            response.setRequestId(requestId);
            response.setTaskIds(taskIds);
            response.setStatus("processing");

            log.info("漫画图片生成任务提交完成，requestId: {}, 任务数: {}", requestId, taskIds.size());
            return response;

        } catch (Exception e) {
            log.error("漫画图片生成任务提交失败", e);
            response.setStatus("failed");
            response.setError(e.getMessage());
            return response;
        }
    }

    @Override
    public ComicQueryResponse getComicImages(String requestId) {
        ComicQueryResponse response = new ComicQueryResponse();

        try {
            // 获取任务ID列表
            List<String> taskIds = requestTaskMap.get(requestId);
            if (taskIds == null || taskIds.isEmpty()) {
                response.setStatus("failed");
                response.setError("未找到对应的任务，requestId: " + requestId);
                return response;
            }

            IVisualService visualService = VisualServiceImpl.getInstance();
            List<String> imageUrls = new ArrayList<>();
            boolean allCompleted = true;

            // 查询每个任务的结果
            for (int i = 0; i < taskIds.size(); i++) {
                String taskId = taskIds.get(i);

                JSONObject req = new JSONObject();
                req.put("req_key", "jimeng_t2i_v30");
                req.put("task_id", taskId);

                Object apiResponse = visualService.cvGetResult(req);
                log.info("cvGetResult response for task {}: {}", taskId, JSON.toJSONString(apiResponse));

                JSONObject jsonResponse = JSON.parseObject(JSON.toJSONString(apiResponse));
                Integer code = jsonResponse.getInteger("code");

                if (code != null && code == 10000) {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    if (data != null) {
                        String status = data.getString("status");

                        if ("done".equals(status)) {
                            // 任务完成，获取图片URL并下载保存
                            String imageUrl = data.getString("image_url");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                String savedImagePath = downloadAndSaveImage(imageUrl, requestId, i + 1);
                                if (savedImagePath != null) {
                                    // 构建访问URL
                                    String fileName = Paths.get(savedImagePath).getFileName().toString();
                                    String accessUrl = imageBaseUrl + "/" + requestId + "/" + fileName;
                                    imageUrls.add(accessUrl);
                                }
                            }
                        } else {
                            allCompleted = false;
                            log.info("任务 {} 状态: {}", taskId, status);
                        }
                    }
                } else {
                    log.error("查询任务 {} 结果失败，code: {}, message: {}",
                            taskId, code, jsonResponse.getString("message"));
                    allCompleted = false;
                }
            }

            // 存储图片路径
            if (!imageUrls.isEmpty()) {
                requestImageMap.put(requestId, imageUrls);
            }

            response.setRequestId(requestId);
            response.setImageUrls(imageUrls);

            if (allCompleted && imageUrls.size() == taskIds.size()) {
                response.setStatus("completed");
            } else if (!imageUrls.isEmpty()) {
                response.setStatus("partial");
            } else {
                response.setStatus("processing");
            }

            log.info("查询漫画图片完成，requestId: {}, 状态: {}, 图片数: {}",
                    requestId, response.getStatus(), imageUrls.size());
            return response;

        } catch (Exception e) {
            log.error("查询漫画图片失败", e);
            response.setStatus("failed");
            response.setError(e.getMessage());
            return response;
        }
    }

    /**
     * 下载并保存图片
     */
    private String downloadAndSaveImage(String imageUrl, String requestId, int index) {
        try {
            // 创建保存目录
            Path saveDir = Paths.get(imageBasePath, requestId);
            if (!Files.exists(saveDir)) {
                Files.createDirectories(saveDir);
            }

            // 生成文件名
            String fileName = "comic_" + index + ".png";
            Path savePath = saveDir.resolve(fileName);

            // 下载图片
            URL url = new URL(imageUrl);
            try (InputStream in = url.openStream();
                 FileOutputStream out = new FileOutputStream(savePath.toFile())) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            log.info("图片下载保存成功: {}", savePath.toString());
            return savePath.toString();

        } catch (Exception e) {
            log.error("图片下载保存失败, url: {}, requestId: {}", imageUrl, requestId, e);
            return null;
        }
    }
}
