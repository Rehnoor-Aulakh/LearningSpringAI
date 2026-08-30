package com.telusko.SpringEcom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatBotController {

    @GetMapping("/ask")
    public ResponseEntity<String> askBot(@RequestParam String message) {
        String response = """
                
                """;
        return ResponseEntity.ok(message);
    }

}
