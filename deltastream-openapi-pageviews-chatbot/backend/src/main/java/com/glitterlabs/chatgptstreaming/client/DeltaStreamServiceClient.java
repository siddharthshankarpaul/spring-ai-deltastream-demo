package com.glitterlabs.chatgptstreaming.client;

import com.deltastream.graphql.model.*;
import com.glitterlabs.chatgptstreaming.configuration.DeltaStreamConfig;
import com.glitterlabs.chatgptstreaming.model.QueryInfo;
import com.glitterlabs.chatgptstreaming.model.UserData;
import com.glitterlabs.chatgptstreaming.service.MilvusService;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeltaStreamServiceClient {
    public static final String BEARER = "Bearer ";
    private final DeltaStreamConfig deltaStreamConfig;
    private RestClient restClient;
    private MilvusService milvusService;

    public DeltaStreamServiceClient(DeltaStreamConfig deltaStreamConfig, MilvusService milvusService) {
        this.deltaStreamConfig = deltaStreamConfig;
        this.milvusService = milvusService;
        this.restClient = RestClient.create(deltaStreamConfig.getDeltaStreamUrl());
    }

    public OrganizationTO getOrganization() {
        log.info("Querying stores and topics");
        OrganizationQueryRequest organizationQueryRequest = new OrganizationQueryRequest();
        organizationQueryRequest.setId(this.deltaStreamConfig.getOrganizationId());
        organizationQueryRequest.setEffectiveRoleName(this.deltaStreamConfig.getDefaultRole());

        OrganizationResponseProjection responseProjection = new OrganizationResponseProjection().stores(new StoreResponseProjection().name().type())
                .databases(
                        new DatabaseResponseProjection().name().schemas(
                                new SchemaResponseProjection().name().relations(
                                        new RelationResponseProjection().name())))
                .queries(new QueryTypeResponseProjection().dsql());
        GraphQLRequest graphQLRequest = new GraphQLRequest(organizationQueryRequest, responseProjection);
        OrganizationQueryResponse resp = restClient.post().uri("query")
                .headers((h) -> addHttpHeaders(h))
                .body(graphQLRequest.toHttpJsonBody())
                .retrieve().body(OrganizationQueryResponse.class);
        if (Objects.nonNull(resp) && resp.hasErrors()) {
            log.error("Oops error occurred {}", resp.getErrors());
        }
        return resp.organization();
    }

    private void addHttpHeaders(HttpHeaders headers) {
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.deltaStreamConfig.getDeltaStreamToken());
    }


    public List<UserData> runStatement() {
        List<UserData> userData = new ArrayList<>();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("orgID", this.deltaStreamConfig.getOrganizationId());
        String databaseName = this.deltaStreamConfig.getDatabaseName();
        map.add("databaseName", databaseName);
        String schemaName = "public";
        map.add("schemaName", schemaName);
        map.add("statement", "select * from " + databaseName + "." + schemaName + "."+this.deltaStreamConfig.getMaterialView()+";");

        ParameterizedTypeReference<List<QueryInfo>> queryInfos = new ParameterizedTypeReference<>() {
        };
        List<QueryInfo> response = restClient.post().uri("run-statement").contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.AUTHORIZATION, BEARER + this.deltaStreamConfig.getDeltaStreamToken())
                .body(map).retrieve().body(queryInfos);
        if (!response.isEmpty()) {
            QueryInfo queryInfo = response.get(0);
            String secret = queryInfo.getSecret();
            if (StringUtils.isNotEmpty(secret)) {
                String url = queryInfo.getData().get(0).getUrl();
                ParameterizedTypeReference<List<UserData>> userDataType = new ParameterizedTypeReference<>() {
                };
                userData = restClient.get().uri(url).header(HttpHeaders.AUTHORIZATION, BEARER + secret).retrieve().body(userDataType);

            }
        }
        return userData;
    }

    public void createPolicy(List<UserData> userData) {
        List<Long> counts = userData.stream().map(UserData::getCnt).sorted().collect(Collectors.toList());
        if (!counts.isEmpty()) {
            double average = counts.stream().mapToLong(v -> v).average().getAsDouble();
            Long midHighAverage = counts.get((counts.size() - counts.size() / 2) + ((counts.size() - counts.size() / 2) / 2));
            Long lowMidAverage = counts.get((counts.size() - counts.size() / 2) / 2);

            milvusService.addPolicy(counts.get(counts.size() - 1), midHighAverage, (long) average, lowMidAverage, counts.get(0) - 10);
        }
    }

}
