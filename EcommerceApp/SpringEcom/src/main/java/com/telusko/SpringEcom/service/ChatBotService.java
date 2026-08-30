package com.telusko.SpringEcom.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatBotService {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private PgVectorStore vectorStore;

    public String getBotResponse(String userQuery) throws IOException {
        try {
            String promptStringTemplate = Files.readString(
                    resourceLoader.getResource("classpath:prompts/chatbot-rag-prompt.st").getFilePath()
            );

            String context = fetchSemanticContext(userQuery);
            Map<String, Object> variables = new HashMap<>();
            variables.put("context", context);
            variables.put("userQuery", userQuery);

            PromptTemplate promptTemplate = PromptTemplate.builder()
                    .template(promptStringTemplate)
                    .variables(variables)
                    .build();

            // send this prompt to LLMs
            String response = chatClient.prompt(promptTemplate.create())
                    .advisors(a -> a.param("chat_memory_conversation_id", "user1"))
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();
            return response;
        } catch (IOException e) {
            return "Bot Failed";
        }

    }

    private String fetchSemanticContext(String userQuery) {
        System.out.println("==== VECTOR SEARCH DEBUG ====");
        System.out.println("User query: " + userQuery);
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(userQuery)
                        .topK(5)
                        .similarityThreshold(0.3)
                        .build()
        );
        System.out.println("Documents found: " + documents.size());
        StringBuilder context = new StringBuilder();
        for(Document document: documents) {
            System.out.println("Doc: " + document.getFormattedContent());
            context.append(document.getFormattedContent()).append("\n");
        }
        System.out.println("==== END DEBUG ====");
        return context.toString();
    }
}
