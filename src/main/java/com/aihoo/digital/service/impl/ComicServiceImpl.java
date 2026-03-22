package com.aihoo.digital.service.impl;

import com.aihoo.digital.dto.GenerateComicRequest;
import com.aihoo.digital.dto.GenerateComicResponse;
import com.aihoo.digital.service.ComicService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.impl.VisualServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ComicServiceImpl implements ComicService {

    @Value("${comic.image.path:/opt/comic/images}")
    private String imagePath;

    @Value("${comic.image.url:http://localhost:8080/api/comic/images}")
    private String imageUrl;

    @Override
    public String generateComicTask(GenerateComicRequest request) {
        String requestId = UUID.randomUUID().toString();
        String content = request.getContent();
        String[] contentParts = content.split("\\|\\|\\|");

        // 异步处理漫画生成
        new Thread(() -> {
            processComicGeneration(requestId, contentParts);
        }).start();

        return requestId;
    }

    @Override
    public void processComicGeneration(String requestId, String[] contentParts) {
        IVisualService visualService = VisualServiceImpl.getInstance();

        // 确保图片存储目录存在
        File dir = new File(imagePath + File.separator + requestId);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (int i = 0; i < contentParts.length && i < 3; i++) {
            try {
                JSONObject req = new JSONObject();
                req.put("req_key", "jimeng_t2i_v30");
                req.put("prompt", contentParts[i].trim());

                Object apiResponse = visualService.cvSubmitTask(req);
                log.info("Comic generation response: {}", JSON.toJSONString(apiResponse));

                JSONObject jsonResponse = JSON.parseObject(JSON.toJSONString(apiResponse));
                Integer code = jsonResponse.getInteger("code");

                if (code != null && code == 10000) {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    if (data != null) {
                        String taskId = data.getString("task_id");
                        log.info("Comic generation task_id for part {}: {}", i+1, taskId);

                        // 轮询获取生成结果
                        byte[] imageData = getComicImageData(visualService, taskId);
                        if (imageData != null) {
                            // 保存图片
                            saveImage(imageData, requestId, "comic_" + (i+1) + ".png");
                        }
                    }
                } else {
                    log.error("Comic generation failed for part {}, code: {}, message: {}", 
                            i+1, code, jsonResponse.getString("message"));
                }
            } catch (Exception e) {
                log.error("Error in processComicGeneration for part {}", i+1, e);
            }
        }
    }

    private byte[] getComicImageData(IVisualService visualService, String taskId) {
        int maxRetries = 20;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                Thread.sleep(30000); // 等待30秒

                JSONObject req = new JSONObject();
                req.put("req_key", "jimeng_t2i_v30");
                req.put("task_id", taskId);

                Object apiResponse = visualService.cvGetResult(req);
                JSONObject jsonResponse = JSON.parseObject(JSON.toJSONString(apiResponse));
                Integer code = jsonResponse.getInteger("code");

                if (code != null && code == 10000) {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    if (data != null) {
                        String status = data.getString("status");
                        if ("done".equals(status)) {
                            String binaryDataBase64 = data.getString("binary_data_base64");
                            if (binaryDataBase64 != null && !binaryDataBase64.isEmpty()) {
                                return java.util.Base64.getDecoder().decode(binaryDataBase64);
                            }
                        } else if ("failed".equals(status)) {
                            log.error("Comic generation task failed: {}", data.getString("message"));
                            return null;
                        }
                    }
                }

                retryCount++;
                log.info("Retrying to get comic image, retry count: {}", retryCount);
            } catch (Exception e) {
                log.error("Error in getComicImageData", e);
                retryCount++;
            }
        }

        log.error("Comic generation timeout after {} retries", maxRetries);
        return null;
    }

    private void saveImage(byte[] imageData, String requestId, String fileName) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(imagePath + File.separator + requestId + File.separator + fileName)) {
            outputStream.write(imageData);
            log.info("Image saved successfully: {}", fileName);
        }
    }

    @Override
    public GenerateComicResponse getComicResult(String requestId) {
        GenerateComicResponse response = new GenerateComicResponse();
        response.setRequestId(requestId);

        List<String> imageUrls = new ArrayList<>();
        File dir = new File(imagePath + File.separator + requestId);

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".png"));
            if (files != null) {
                for (File file : files) {
                    String imageUrl = this.imageUrl + "/" + requestId + "/" + file.getName();
                    imageUrls.add(imageUrl);
                }
            }
        }

        response.setImageUrls(imageUrls);
        return response;
    }
}
