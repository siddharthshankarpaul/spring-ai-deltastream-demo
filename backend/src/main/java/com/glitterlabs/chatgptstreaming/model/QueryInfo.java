package com.glitterlabs.chatgptstreaming.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QueryInfo {

    @JsonProperty("action")
    private String action;

    @JsonProperty("target")
    private String target;

    @JsonProperty("id")
    private String id;

    @JsonProperty("secret")
    private String secret;

    @JsonProperty("data")
    private List<QueryData> data;
}

