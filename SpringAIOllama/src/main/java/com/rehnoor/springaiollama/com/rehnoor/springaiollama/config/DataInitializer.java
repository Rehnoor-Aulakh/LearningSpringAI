package com.rehnoor.springaiollama.config;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer {


    @Autowired
    private VectorStore vectorStore;

    @PostConstruct
    public void initData() {
        TextReader textReader = new TextReader(new ClassPathResource("product-details.txt"));
        String content = textReader.get().get(0).getText();
        List<Document> documents = java.util.Arrays.stream(content.split("\\n\\n"))
                .filter(text -> !text.trim().isEmpty())
                .map(Document::new)
                .toList();

        vectorStore.add(documents);
    }
}
