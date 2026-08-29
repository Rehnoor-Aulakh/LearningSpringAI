package com.rehnoor.springaiollama.config;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer {

    private final VectorStore vectorStore;

    public DataInitializer(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void initData() {

        TextReader textReader =
                new TextReader(
                        new ClassPathResource("product-details.txt")
                );

        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(100)
                .withMinChunkSizeChars(30)
                .withMinChunkLengthToEmbed(5)
                .withMaxNumChunks(500)
                .withKeepSeparator(false)
                .build();

        List<Document> documents =
                splitter.apply(textReader.get());

        vectorStore.add(documents);
    }
}