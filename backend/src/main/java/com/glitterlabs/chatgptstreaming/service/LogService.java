package com.glitterlabs.chatgptstreaming.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;

@Service
@Slf4j
public class LogService {
    private ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(20);

    public void addToQueue(String logEvent) {
        try {
            queue.put(logEvent);
        } catch (InterruptedException e) {
            log.error("Error while adding to queue", e);
        }
    }

    public String take() {
        try {
            return this.queue.take();
        } catch (InterruptedException e) {
            log.error("Error while taking from queue", e);
        }
        return null;
    }
}
