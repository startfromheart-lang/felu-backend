package com.aihoo.digital.service;

import com.aihoo.digital.dto.ComicGenerateRequest;
import com.aihoo.digital.dto.ComicGenerateResponse;
import com.aihoo.digital.dto.ComicQueryResponse;
import com.aihoo.digital.dto.ComicSubmitResponse;

public interface ComicService {

    /**
     * 第一步：解析用户输入内容，分成三段，生成请求唯一ID
     *
     * @param request 漫画生成请求
     * @return 包含分段内容和请求ID的响应
     */
    ComicGenerateResponse parseContent(ComicGenerateRequest request);

    /**
     * 第二步：调用文生图接口，异步生成三张漫画图片
     *
     * @param requestId 请求唯一ID
     * @return 提交任务响应
     */
    ComicSubmitResponse generateComicImages(String requestId);

    /**
     * 第三步：基于请求ID读取生成的图片，并返回图片网址
     *
     * @param requestId 请求唯一ID
     * @return 图片查询响应
     */
    ComicQueryResponse getComicImages(String requestId);
}
