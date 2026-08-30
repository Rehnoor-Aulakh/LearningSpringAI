package com.telusko.SpringEcom.service;

import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.stabilityai.StabilityAiImageModel;
import org.springframework.ai.stabilityai.StyleEnum;
import org.springframework.ai.stabilityai.api.StabilityAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class AiImageGenService {

    @Autowired
    private StabilityAiImageModel imageModel;

    public byte[] generateAIImage(String query) throws Exception {
        ImagePrompt prompt = new ImagePrompt(query, StabilityAiImageOptions.builder()
                .n(1)
                .steps(30)
                .height(1024)
                .width(1024)
                .stylePreset(StyleEnum.PHOTOGRAPHIC)
                .build()
        );
        ImageResponse response = imageModel.call(prompt);
        String b64Json = response.getResult().getOutput().getB64Json();

        byte[] imageBytes = Base64.getDecoder().decode(b64Json);
        if(imageBytes!=null) {
            return imageBytes;
        } else {
            throw new Exception("Image is Null");
        }

    }
}
