package com.glitterlabs.chatgptstreaming.controller;

import com.deltastream.graphql.model.DatabaseTO;
import com.deltastream.graphql.model.OrganizationTO;
import com.deltastream.graphql.model.QueryTypeTO;
import com.deltastream.graphql.model.StoreTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.glitterlabs.chatgptstreaming.client.DeltaStreamServiceClient;
import com.glitterlabs.chatgptstreaming.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("deltastream")
@Slf4j
@RequiredArgsConstructor
public class DeltaStreamController {

    private final DeltaStreamServiceClient deltaStreamServiceClient;

    private final LogService logService;

    @GetMapping(value = "/organization", produces = "application/json")
    public OrganizationTO orgData() throws JsonProcessingException {
        logService.addToQueue("Fetch stores from DeltaStream");
        OrganizationTO organizationTO = deltaStreamServiceClient.getOrganization();
        List<StoreTO> stores = organizationTO.getStores();
        List<DatabaseTO> databases = Optional.ofNullable(organizationTO.getDatabases()).orElse(List.of());
        List<QueryTypeTO> queries = Optional.ofNullable(organizationTO.getQueries()).orElse(List.of());
        logService.addToQueue("Received " + stores.size() + " stores and " + databases.size() + " databases from DeltaStream");
        return organizationTO;
    }

}
