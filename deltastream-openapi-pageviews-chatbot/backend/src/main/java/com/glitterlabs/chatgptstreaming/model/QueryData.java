package com.glitterlabs.chatgptstreaming.model;

public record QueryData(String url) {
    public String getUrl() {
        return url;
    }
}
