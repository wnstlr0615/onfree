package com.onfree.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.core.dto.external.toss.payment.PaymentDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestTemplateUtils {
    private final RestTemplateBuilder restTemplateBuilder;
    private final ObjectMapper mapper;
    private RestTemplate getRestTemplate(){
        return restTemplateBuilder
                .build();
    }

    public ResponseEntity<String> sendRequest(RequestEntity<?> requestEntity){
        RestTemplate restTemplate = getRestTemplate();
        ResponseEntity<String> responseEntity = null;

        try {
            responseEntity = restTemplate.exchange(requestEntity, String.class);
        } catch (RestClientException e) {
            log.error("RestTemplate Error : ", e);
            e.printStackTrace();
        }
        return responseEntity;
    }

    public RequestEntity<String> getPostRequestEntity(String requestURI, Object body) {
        RequestEntity<String> requestEntity = null;
        try {
            requestEntity = RequestEntity.post(requestURI)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(
                            mapper.writeValueAsString(body)
                    );
        } catch (JsonProcessingException e) {
            log.error("RestTemplate Error");
            log.error("JsonProcessingException ", e);
            e.printStackTrace();
        }
        return requestEntity;
    }
}
