package com.onfree.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleResponse  extends RepresentationModel<SimpleResponse> {
    private final boolean result;
    private final String message;


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
