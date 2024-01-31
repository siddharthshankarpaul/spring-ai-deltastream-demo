package com.glitterlabs.chatgptstreaming.controller;

import com.glitterlabs.chatgptstreaming.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

@Controller
@CrossOrigin("http://localhost:5173")
@Slf4j
public class LogController {

    private final LogService logService;
    private Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    private LogController(LogService logService) {
        this.logService = logService;
        initializeEmitters();
    }

    private static void handleException(SseEmitter emitter, Exception e) {
        log.error("Error while sending event", e.getMessage());
        emitter.completeWithError(e);
    }

    @GetMapping(path = "/stream-log")
    public SseEmitter streamLog() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitter.onCompletion(() -> {
            emitters.remove(emitter.hashCode());
        });
        emitter.onTimeout(() -> {
            emitter.complete();  // Explicitly complete the emitter on timeout
        });
        emitters.put(emitter.hashCode(), emitter);
        try {
            send(emitter, ""); // send to start the connection
        } catch (IOException e) {
            handleException(emitter, e);
        }
        return emitter;
    }

    private void initializeEmitters() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Thread.currentThread().setName("Cust-Emitter" + new Random().nextInt());
            String event;
            Map<SseEmitter, Exception> staleEmitters = new HashMap<>();
            while ((event = this.logService.take()) != null) {
                String finalEvent = event;
                emitters.values().forEach(emitter -> {
                    try {
                        send(emitter, finalEvent);
                    } catch (Exception e) {
                        staleEmitters.put(emitter, e);
                    }
                });
                staleEmitters.forEach(LogController::handleException);
                staleEmitters.clear();
            }
        });
    }

    private void send(SseEmitter emitter, String event) throws IOException {
        emitter.send(SseEmitter.event().id(String.valueOf(emitter.hashCode())).data(event));
    }
}
