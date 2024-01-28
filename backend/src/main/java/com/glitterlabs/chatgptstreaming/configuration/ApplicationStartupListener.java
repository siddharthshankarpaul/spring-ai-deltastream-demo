package com.glitterlabs.chatgptstreaming.configuration;

import com.glitterlabs.chatgptstreaming.client.DeltaStreamServiceClient;
import com.glitterlabs.chatgptstreaming.model.UserData;
import com.glitterlabs.chatgptstreaming.service.MilvusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ApplicationStartupListener {

    @EventListener(ApplicationStartedEvent.class)
    public void onStart(ApplicationStartedEvent event) {
        ConfigurableApplicationContext context = event.getApplicationContext();
        DeltaStreamServiceClient deltaStreamServiceClient = context.getBean(DeltaStreamServiceClient.class);
        MilvusService milvusService = context.getBean(MilvusService.class);
        List<UserData> userData = deltaStreamServiceClient.runStatement();
        log.info("Queried data from deltastream {}", userData);

        milvusService.dropCollection();
        log.info("Drop collection from vectordb");

        milvusService.createAndIndexAndLoadCollection();

        deltaStreamServiceClient.createPolicy(userData);
        log.info("Created policy");
    }
}
