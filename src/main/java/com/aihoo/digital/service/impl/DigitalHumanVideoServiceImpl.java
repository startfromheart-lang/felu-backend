package com.aihoo.digital.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.aihoo.digital.dto.GenerateVideoRequest;
import com.aihoo.digital.dto.GenerateVideoResponse;
import com.aihoo.digital.dto.ObjectDetectRequest;
import com.aihoo.digital.dto.ObjectDetectResponse;
import com.aihoo.digital.dto.ObjectRecognizeRequest;
import com.aihoo.digital.dto.ObjectRecognizeResponse;
import com.aihoo.digital.dto.RecognizeQueryResponse;
import com.aihoo.digital.dto.RecognizeQueryResult;
import com.aihoo.digital.dto.VideoGenerateRequest;
import com.aihoo.digital.dto.VideoGenerateResponse;
import com.aihoo.digital.dto.VideoQueryResponse;
import com.aihoo.digital.entity.DigitalHumanVideo;
import com.aihoo.digital.mapper.DigitalHumanVideoMapper;
import com.aihoo.digital.service.DigitalHumanVideoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.impl.VisualServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DigitalHumanVideoServiceImpl implements DigitalHumanVideoService {

    private final DigitalHumanVideoMapper videoMapper;
    private final WebClient.Builder webClientBuilder;


    private RecognizeQueryResponse getRecognizeResultInternal(String taskId) {
        IVisualService visualService = VisualServiceImpl.getInstance();
        // call below method if you dont set ak and sk in ～/.vcloud/config

        JSONObject req = new JSONObject();
        //请求Body(查看接口文档请求参数-请求示例，将请求参数内容复制到此)
        req.put("req_key","jimeng_realman_avatar_picture_create_role_omni_v15");
        req.put("task_id", taskId);

        RecognizeQueryResponse response = new RecognizeQueryResponse();

        try {
            Object apiResponse = visualService.cvGetResult(req);
            System.out.println(JSON.toJSONString(apiResponse));

            // 解析响应，提取状态和视频 URL
            JSONObject jsonResponse = JSON.parseObject(JSON.toJSONString(apiResponse));
            Integer code = jsonResponse.getInteger("code");

            if (code != null && code == 10000) {
                JSONObject data = jsonResponse.getJSONObject("data");
                if (data != null) {
                    String status = data.getString("status");
                    log.info("Task status: {}", status);

                    response.setStatus(status);

                    // 如果 status 为 done，则解析 resp_data 中的 status
                    if ("done".equals(status)) {
                        String respDataStr = data.getString("resp_data");
                        if (respDataStr != null && !respDataStr.isEmpty()) {
                            JSONObject respData = JSON.parseObject(respDataStr);
                            Integer respDataStatus = respData.getInteger("status");
                            response.setRespDataStatus(respDataStatus);
                        }
                    }

                    return response;
                }
            }

            log.error("API returned error code: {}, message: {}", code, jsonResponse.getString("message"));
            response.setStatus("failed");
            response.setError(jsonResponse.getString("message"));
            return response;
        } catch (Exception e) {
            log.error("Error in getRecognizeResultInternal", e);
            e.printStackTrace();
            response.setStatus("failed");
            response.setError(e.getMessage());
            return response;
        }
    }








    private VideoQueryResponse getVideoUrlInternal(String taskId) {
        IVisualService visualService = VisualServiceImpl.getInstance();
        // call below method if you dont set ak and sk in ～/.vcloud/config

        JSONObject req = new JSONObject();
        //请求Body(查看接口文档请求参数-请求示例，将请求参数内容复制到此)
        req.put("req_key","jimeng_realman_avatar_picture_omni_v15");
        req.put("task_id", taskId);

        VideoQueryResponse response = new VideoQueryResponse();

        try {
            Object apiResponse = visualService.cvGetResult(req);
            System.out.println(JSON.toJSONString(apiResponse));

            // 解析响应，提取状态和视频 URL
            JSONObject jsonResponse = JSON.parseObject(JSON.toJSONString(apiResponse));
            Integer code = jsonResponse.getInteger("code");

            if (code != null && code == 10000) {
                JSONObject data = jsonResponse.getJSONObject("data");
                if (data != null) {
                    String status = data.getString("status");
                    log.info("Task status: {}", status);

                    response.setStatus(status);
                    response.setVideoUrl(data.getString("video_url"));
                    return response;
                }
            }

            log.error("API returned error code: {}, message: {}", code, jsonResponse.getString("message"));
            response.setStatus("failed");
            response.setError(jsonResponse.getString("message"));
            return response;
        } catch (Exception e) {
            log.error("Error in getVideoUrlInternal", e);
            e.printStackTrace();
            response.setStatus("failed");
            response.setError(e.getMessage());
            return response;
        }
    }










    @Override
    public DigitalHumanVideo getVideoDetail(String id) {
        try {
            return videoMapper.selectById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.error("Invalid video ID format: {}", id);
            return null;
        }
    }

    @Override
    public List<DigitalHumanVideo> getVideoList(Integer page, Integer pageSize) {
        Page<DigitalHumanVideo> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<DigitalHumanVideo> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(DigitalHumanVideo::getCreateTime);

        Page<DigitalHumanVideo> result = videoMapper.selectPage(pageParam, wrapper);
        return result.getRecords();
    }

    @Override
    public ObjectRecognizeResponse objectRecognize(ObjectRecognizeRequest request) {
        ObjectRecognizeResponse response = new ObjectRecognizeResponse();

        try {
            IVisualService visualService = VisualServiceImpl.getInstance();

            JSONObject req = new JSONObject();
            req.put("req_key", "jimeng_realman_avatar_picture_create_role_omni_v15");
            req.put("image_url", "https://static.heouai.com/founder-zhou-zhizhang.png");

            Object apiResponse = visualService.cvSubmitTask(req);
            System.out.println(JSON.toJSONString(apiResponse));

            JSONObject jsonResponse = JSON.parseObject(JSON.toJSONString(apiResponse));
            Integer code = jsonResponse.getInteger("code");

            if (code != null && code == 10000) {
                JSONObject data = jsonResponse.getJSONObject("data");
                if (data != null) {
                    String taskId = data.getString("task_id");
                    response.setTaskId(taskId);
                    response.setStatus("processing");
                    log.info("Object recognize task_id: {}", taskId);
                    return response;
                }
            }

            log.error("Object recognize failed, code: {}, message: {}", code, jsonResponse.getString("message"));
            response.setStatus("failed");
            response.setError(jsonResponse.getString("message"));
            return response;
        } catch (Exception e) {
            log.error("Error in objectRecognize", e);
            response.setStatus("failed");
            response.setError(e.getMessage());
            return response;
        }
    }

    @Override
    public RecognizeQueryResponse getRecognizeResult(String taskId) {
        return getRecognizeResultInternal(taskId);
    }

    @Override
    public ObjectDetectResponse objectDetect(ObjectDetectRequest request) {
        ObjectDetectResponse response = new ObjectDetectResponse();

        try {
            IVisualService visualService = VisualServiceImpl.getInstance();

            JSONObject req = new JSONObject();
            req.put("req_key", "jimeng_realman_avatar_object_detection");
            req.put("image_url", "https://static.heouai.com/founder-zhou-zhizhang.png");

            Object apiResponse = visualService.cvProcess(req);
            System.out.println(JSON.toJSONString(apiResponse));

            JSONObject jsonResponse = JSON.parseObject(JSON.toJSONString(apiResponse));
            Integer code = jsonResponse.getInteger("code");

            if (code != null && code == 10000) {
                JSONObject data = jsonResponse.getJSONObject("data");
                if (data != null) {
                    String taskId = data.getString("task_id");
                    response.setTaskId(taskId);
                    response.setStatus("processing");

                    // 模拟返回图片URL列表（实际应该从API响应中获取）
                    List<String> imageUrls = new ArrayList<>();
                    imageUrls.add(request.getImageUrl());
                    response.setImageUrls(imageUrls);

                    log.info("Object detect task_id: {}", taskId);
                    return response;
                }
            }

            log.error("Object detect failed, code: {}, message: {}", code, jsonResponse.getString("message"));
            response.setStatus("failed");
            response.setError(jsonResponse.getString("message"));
            return response;
        } catch (Exception e) {
            log.error("Error in objectDetect", e);
            response.setStatus("failed");
            response.setError(e.getMessage());
            return response;
        }
    }

    @Override
    public GenerateVideoResponse generateVideo(GenerateVideoRequest request) {
        GenerateVideoResponse response = new GenerateVideoResponse();

        try {
            IVisualService visualService = VisualServiceImpl.getInstance();

            JSONObject req = new JSONObject();
            req.put("req_key", "jimeng_realman_avatar_picture_omni_v15");
            req.put("image_url", "https://static.heouai.com/founder-zhou-zhizhang.png");
            req.put("audio_url", "https://static.heouai.com/voice.m4a");

            Object apiResponse = visualService.cvSubmitTask(req);
            System.out.println(JSON.toJSONString(apiResponse));

            JSONObject jsonResponse = JSON.parseObject(JSON.toJSONString(apiResponse));
            Integer code = jsonResponse.getInteger("code");

            if (code != null && code == 10000) {
                JSONObject data = jsonResponse.getJSONObject("data");
                if (data != null) {
                    String taskId = data.getString("task_id");
                    response.setTaskId(taskId);
                    response.setStatus("processing");
                    log.info("Generate video task_id: {}", taskId);
                    return response;
                }
            }

            log.error("Generate video failed, code: {}, message: {}", code, jsonResponse.getString("message"));
            response.setStatus("failed");
            response.setError(jsonResponse.getString("message"));
            return response;
        } catch (Exception e) {
            log.error("Error in generateVideo", e);
            response.setStatus("failed");
            response.setError(e.getMessage());
            return response;
        }
    }

    @Override
    public VideoQueryResponse getVideoUrl(String taskId) {
        return getVideoUrlInternal(taskId);
    }








	@Override
	public VideoGenerateResponse doGenerateVideo(VideoGenerateRequest request) {
		// 使用默认图片URL，实际应该从request中获取或从数据库查询
		String imageUrl = "https://static.heouai.com/founder-zhou-zhizhang.png";
		String audioUrl = "https://static.heouai.com/voice.m4a";

		VideoGenerateResponse response = new VideoGenerateResponse();

		try {
			// 1. 调用objectRecognize进行主体识别
			ObjectRecognizeRequest recognizeRequest = new ObjectRecognizeRequest();
			recognizeRequest.setImageUrl(imageUrl);
			ObjectRecognizeResponse recognizeResponse = objectRecognize(recognizeRequest);

			if (recognizeResponse == null || "failed".equals(recognizeResponse.getStatus())) {
				log.error("Object recognize failed: {}", recognizeResponse != null ? recognizeResponse.getError() : "null response");
				response.setStatus("failed");
				response.setError("Object recognize failed");
				return response;
			}

			String recognizeTaskId = recognizeResponse.getTaskId();
			log.info("Object recognize task_id: {}", recognizeTaskId);

			// 2. 轮询等待识别结果
			int maxRetries = 10;
			int retryCount = 0;
			RecognizeQueryResult recognizeResult = null;

			while (retryCount < maxRetries) {
				try {
					Thread.sleep(60000); // 等待1分钟
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}

				RecognizeQueryResponse queryResponse = getRecognizeResult(recognizeTaskId);

				if (queryResponse != null && "done".equals(queryResponse.getStatus())) {
					recognizeResult = new RecognizeQueryResult();
					recognizeResult.setStatus(queryResponse.getStatus());
					recognizeResult.setRespDataStatus(queryResponse.getRespDataStatus());
					log.info("Object recognize completed, resp_data status: {}", queryResponse.getRespDataStatus());
					break;
				} else if (queryResponse != null) {
					log.info("Object recognize status: {}", queryResponse.getStatus());
				}

				retryCount++;
			}

			if (recognizeResult == null || !"done".equals(recognizeResult.getStatus())) {
				log.error("Object recognize timeout or failed after {} retries", maxRetries);
				response.setStatus("failed");
				response.setError("Object recognize timeout");
				return response;
			}

			// 3. 调用objectDetect进行客体检测
			ObjectDetectRequest detectRequest = new ObjectDetectRequest();
			detectRequest.setImageUrl(imageUrl);
			ObjectDetectResponse detectResponse = objectDetect(detectRequest);

			if (detectResponse == null || "failed".equals(detectResponse.getStatus())) {
				log.error("Object detect failed: {}", detectResponse != null ? detectResponse.getError() : "null response");
				response.setStatus("failed");
				response.setError("Object detect failed");
				return response;
			}

			String detectTaskId = detectResponse.getTaskId();
			log.info("Object detect task_id: {}", detectTaskId);

			// 4. 生成视频
			GenerateVideoRequest generateRequest = new GenerateVideoRequest();
			generateRequest.setImageUrl(imageUrl);
			generateRequest.setAudioUrl(audioUrl);
			generateRequest.setVoiceType(request.getVoiceType());
			GenerateVideoResponse generateResponse = generateVideo(generateRequest);

			if (generateResponse == null || "failed".equals(generateResponse.getStatus())) {
				log.error("Generate video failed: {}", generateResponse != null ? generateResponse.getError() : "null response");
				response.setStatus("failed");
				response.setError("Generate video failed");
				return response;
			}

			String generateTaskId = generateResponse.getTaskId();
			log.info("Generate video task_id: {}", generateTaskId);

			// 5. 轮询等待视频生成结果
			retryCount = 0;
			VideoQueryResponse videoQueryResponse = null;

			while (retryCount < maxRetries) {
				try {
					Thread.sleep(60000); // 等待1分钟
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}

				videoQueryResponse = getVideoUrl(generateTaskId);

				if (videoQueryResponse != null && "done".equals(videoQueryResponse.getStatus())) {
					log.info("Video generation completed, video_url: {}", videoQueryResponse.getVideoUrl());
					response.setStatus("completed");
					response.setVideoUrl(videoQueryResponse.getVideoUrl());
					return response;
				} else if (videoQueryResponse != null) {
					log.info("Video generation status: {}", videoQueryResponse.getStatus());
				}

				retryCount++;
			}

			log.error("Video generation timeout or failed after {} retries", maxRetries);
			response.setStatus("failed");
			response.setError("Video generation timeout");
			return response;
		} catch (Exception e) {
			log.error("Error in doGenerateVideo", e);
			response.setStatus("failed");
			response.setError(e.getMessage());
			return response;
		}
	}
}
