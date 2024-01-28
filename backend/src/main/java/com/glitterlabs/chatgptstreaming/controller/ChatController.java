package com.glitterlabs.chatgptstreaming.controller;

import com.glitterlabs.chatgptstreaming.client.DeltaStreamServiceClient;
import com.glitterlabs.chatgptstreaming.model.ChatRequest;
import com.glitterlabs.chatgptstreaming.model.UserData;
import com.glitterlabs.chatgptstreaming.service.ChatgptService;
import com.glitterlabs.chatgptstreaming.service.LogService;
import com.glitterlabs.chatgptstreaming.service.MilvusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@CrossOrigin("http://localhost:5173")
@Slf4j
public class ChatController {

    @Autowired
    private ChatgptService chatService;

    @Autowired
    private LogService logService;

    @Autowired
    private MilvusService milvusService;

    @Autowired
    private DeltaStreamServiceClient deltaStreamServiceClient;

    @PostMapping(value = "/chat/text", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAIChatResponse(@RequestBody ChatRequest payload) {
        log.info("Received customer question: {}", payload.getQuery());
        logService.addToQueue("Received query " + payload.getQuery());
        String userData = this.deltaStreamServiceClient.runStatement().stream().map(UserData::toString).collect(Collectors.joining(","));
        String response = chatService.getAssistantResponse(payload.getQuery(), userData);
        logService.addToQueue("Received response from OpenAI");
        return response;
    }

}
