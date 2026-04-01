package com.example.spring_security.controller.user;
import com.example.spring_security.LLM.ChatAssistantService;
import com.example.spring_security.dto.request.RefineRequest;
import com.example.spring_security.dto.request.SuggestRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat/user/assistant")
@RequiredArgsConstructor
public class ChatAssistantController {

    @Autowired
    private final ChatAssistantService chatAssistantService;

    @PostMapping("/refine")
    public ResponseEntity<String> refineText(@RequestBody RefineRequest refineRequest) {

        String originalText = refineRequest.getText();

        String tone = refineRequest.getTone();

        String refined = chatAssistantService.refineText(originalText, tone);
        return ResponseEntity.ok(refined);
    }


    @PostMapping("/suggest")
    public ResponseEntity<List<String>> suggestReply(@RequestBody SuggestRequest suggestRequest) {
        String incomingMessage = suggestRequest.getMessage();

        List<String> suggestions = chatAssistantService.suggestReplies(incomingMessage);
        return ResponseEntity.ok(suggestions);
    }
}