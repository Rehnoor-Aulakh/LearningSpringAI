package com.rehnoor.springaiollama.controller;

import com.rehnoor.springaiollama.Movie;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class MovieController {

    private ChatClient chatClient;

    ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

    public MovieController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @GetMapping("movies")
    public List<String> getMovies(@RequestParam String name) {
        String message = """
                    List top 5 movies of {name}
                    {format}
                """;
        ListOutputConverter outputConverter = new ListOutputConverter(new DefaultConversionService());
        PromptTemplate template = PromptTemplate.builder().template(message).variables(Map.of("name", name, "format", outputConverter.getFormat())).build();
        Prompt prompt = template.create();
        List<String> movies = outputConverter.convert(
                chatClient
                        .prompt(prompt)
                        .advisors(a -> a.param(
                                ChatMemory.CONVERSATION_ID,
                                "user1"
                        ))
                        .call()
                        .content()
        );
        return movies;
    }

    @GetMapping("/movie")
     public Movie getMovieData(@RequestParam String name) {
        String message = """
                    Get me the best movie of {name}
                    {format}
                """;
        BeanOutputConverter<Movie> outputConverter = new BeanOutputConverter<>(Movie.class );

        PromptTemplate template = PromptTemplate.builder().template(message).variables(Map.of("name", name, "format", outputConverter.getFormat())).build();
        Prompt prompt = template.create();
        Movie movie = outputConverter.convert(
                chatClient.prompt(prompt)
                        .advisors(a -> a.param(
                                ChatMemory.CONVERSATION_ID,
                                "user1"
                        ))
                        .call()
                        .content()
        );
        return movie;
     }
    @GetMapping("/moviesList")
    public List<Movie> getMoviesList(@RequestParam String name) {
        String message = """
                    Top 5 movies of {name}
                    {format}
                """;
        BeanOutputConverter<List<Movie>> outputConverter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<Movie>>() {

                }
        );

        PromptTemplate template = PromptTemplate.builder().template(message).variables(Map.of("name", name, "format", outputConverter.getFormat())).build();
        Prompt prompt = template.create();
        List<Movie> movies = outputConverter.convert(
                chatClient.prompt(prompt)
                        .advisors(a -> a.param(
                                ChatMemory.CONVERSATION_ID,
                                "user1"
                        ))
                        .call()
                        .content()
        );
        return movies;
    }
}
