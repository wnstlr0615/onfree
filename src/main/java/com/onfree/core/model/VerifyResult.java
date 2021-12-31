package com.onfree.core.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VerifyResult {
    private final boolean result;
    private final String username;

    public static VerifyResult success(String username){
        return VerifyResult.builder()
                .result(true)
                .username(username)
                .build();
    }

    public static VerifyResult expired(String username){
        return VerifyResult.builder()
                .result(false)
                .username(username)
                .build();
    }
}
