package com.rehnoor.springaicode;

import com.openai.client.OpenAIClient;
import com.openai.client.OpenAIClientImpl;
import com.openai.core.ClientOptions;
import com.openai.core.http.HttpClient;
import com.openai.core.http.HttpRequest;
import com.openai.core.http.HttpResponse;
import com.openai.core.RequestOptions;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import org.springframework.ai.openai.http.okhttp.SpringAiOpenAiHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Configuration
public class OpenAiConfig {

    @Bean
    public OpenAIClient openAIClient(
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.base-url}") String baseUrl) {

        // Build the SpringAiOpenAiHttpClient with timeout
        SpringAiOpenAiHttpClient springHttpClient = SpringAiOpenAiHttpClient.builder()
                .timeout(Duration.ofMinutes(3))
                .build();

        // Rebuild the underlying OkHttpClient with HTTP/1.1 protocol forced
        OkHttpClient http1OnlyClient = springHttpClient.getOkHttpClient()
                .newBuilder()
                .protocols(List.of(Protocol.HTTP_1_1))
                .build();

        // Replace the internal OkHttpClient via reflection
        try {
            Field field = SpringAiOpenAiHttpClient.class.getDeclaredField("okHttpClient");
            field.setAccessible(true);
            field.set(springHttpClient, http1OnlyClient);
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure HTTP/1.1 for OpenRouter", e);
        }

        ClientOptions clientOptions = ClientOptions.builder()
                .httpClient(springHttpClient)
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .timeout(Duration.ofMinutes(3))
                .build();

        return new OpenAIClientImpl(clientOptions);
    }
}
