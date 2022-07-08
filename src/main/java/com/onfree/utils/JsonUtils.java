package com.onfree.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonUtils {
    private final ObjectMapper mapper;
    public String toJson(Object o) throws JsonProcessingException {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("fail JsonUtils toJson Error - transfer type : {}", o.getClass().getName());
            throw e;
        }
    }

    public <T> T fromJson(String json, Class<T> responseType) throws JsonProcessingException {
        try {
            return mapper.readValue(json, responseType);
        } catch (JsonProcessingException e) {
            log.error("fail JsonUtils fromJson Error - transfer type : {}", responseType.getName());
            throw e;
        }
    }
}
