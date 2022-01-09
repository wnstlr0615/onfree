package com.onfree.common.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SimpleResponse {
    private boolean result;
    private String message;

    public static SimpleResponse success(String message){
        return SimpleResponse.builder()
                .result(true)
                .message(message)
                .build();
    }

    public static SimpleResponse fail(String message){
        return SimpleResponse.builder()
                .result(false)
                .message(message)
                .build();
    }
}
