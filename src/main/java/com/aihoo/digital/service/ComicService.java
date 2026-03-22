package com.aihoo.digital.service;

import com.aihoo.digital.dto.GenerateComicRequest;
import com.aihoo.digital.dto.GenerateComicResponse;

public interface ComicService {
    String generateComicTask(GenerateComicRequest request);
    void processComicGeneration(String requestId, String[] contentParts);
    GenerateComicResponse getComicResult(String requestId);
}
