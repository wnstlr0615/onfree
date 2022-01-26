package com.onfree.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleResponse {
    private boolean result;
    private String message;
    private Object data;

    public static SimpleResponse success(String message){
        return SimpleResponse.builder()
                .result(true)
                .message(message)
                .build();
    }
    public static SimpleResponse success(String message, Object data){
        return SimpleResponse.builder()
                .result(true)
                .message(message)
                .data(data)
                .build();
    }

    public static SimpleResponse fail(String message){
        return SimpleResponse.builder()
                .result(false)
                .message(message)
                .build();
    }
}
