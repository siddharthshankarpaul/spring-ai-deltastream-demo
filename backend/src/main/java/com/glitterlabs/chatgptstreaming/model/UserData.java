package com.glitterlabs.chatgptstreaming.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserData {
    private String userid;
    private Long cnt;

    @Override
    public String toString() {
        return "Userid=" + userid + "" + " view=" + cnt;
    }
}
