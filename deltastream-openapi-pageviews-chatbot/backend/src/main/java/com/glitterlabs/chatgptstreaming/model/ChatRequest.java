package com.glitterlabs.chatgptstreaming.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatRequest implements Serializable {
    private String query;
}
