package com.rehnoor.springaiollama.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.image.*;
import org.springframework.ai.stabilityai.StabilityAiImageModel;
import org.springframework.ai.stabilityai.StyleEnum;
import org.springframework.ai.stabilityai.api.StabilityAiImageOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.Base64;

@RestController
public class ImageGenController {
    private ChatClient chatClient;

    private StabilityAiImageModel imageModel;

    public ImageGenController(ChatClient.Builder builder, StabilityAiImageModel imageModel) {
        this.chatClient = builder.build();
        this.imageModel = imageModel;
    }

    @GetMapping(value = "/image/{query}")
    public ResponseEntity<?> genImage(@PathVariable String query) {
        ImagePrompt prompt = new ImagePrompt(query, StabilityAiImageOptions.builder()
                .steps(30)
                .height(1024)
                .width(1024)
                .stylePreset("cinematic")
                .build());
        ImageResponse response = imageModel.call(prompt);
        
        String b64Json = response.getResult().getOutput().getB64Json();
        if (b64Json != null) {
            byte[] imageBytes = Base64.getDecoder().decode(b64Json);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);
        }
        
        return ResponseEntity.ok(response.getResult().getOutput().getUrl());
    }

}
