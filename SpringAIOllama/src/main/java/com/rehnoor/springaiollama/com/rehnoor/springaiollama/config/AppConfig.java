package com.rehnoor.springaiollama.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.RedisClient;

@Configuration
public class AppConfig {

    @Bean
    public RedisClient jedisClient() {
        return RedisClient.builder().hostAndPort("localhost", 6379).build();
    }

    @Bean
    public VectorStore vectorStore(RedisClient jedisClient, EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisClient,embeddingModel)
                .indexName("product-index")
                .initializeSchema(true)
                .build();
    }
}
