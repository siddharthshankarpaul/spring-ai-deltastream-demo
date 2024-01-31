package com.glitterlabs.chatgptstreaming.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class DeltaStreamConfig {

    @Value("${deltastream.api.url}")
    private String deltaStreamUrl;

    @Value("${deltastream.token}")
    private String deltaStreamToken;

    @Value("${deltastream.organization.id}")
    private String organizationId;

    @Value("${deltastream.databaseName}")
    private String databaseName;

    @Value("${deltastream.materialView}")
    private String materialView;

    @Value("${deltastream.default.role}")
    private String defaultRole;

}
