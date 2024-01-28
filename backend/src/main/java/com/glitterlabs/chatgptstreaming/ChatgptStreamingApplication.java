package com.glitterlabs.chatgptstreaming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ChatgptStreamingApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ChatgptStreamingApplication.class, args);
    }

}
