package com.rehnoor.springaiollama.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.Base64;

@RestController
public class ImageGenController {
    private ChatClient chatClient;

    private ImageModel imageModel;

    public ImageGenController(ChatClient.Builder builder, ImageModel imageModel) {
        this.chatClient = builder.build();
        this.imageModel = imageModel;
    }

    @GetMapping(value = "/image/{query}")
    public ResponseEntity<?> genImage(@PathVariable String query) {
        ImagePrompt prompt = new ImagePrompt(query);
        ImageResponse response = imageModel.call(prompt);
        
        String b64Json = response.getResult().getOutput().getB64Json();
        if (b64Json != null) {
            byte[] imageBytes = Base64.getDecoder().decode(b64Json);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);
        }
        
        return ResponseEntity.ok(response.getResult().getOutput().getUrl());
    }

}
