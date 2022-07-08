package com.onfree.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.core.dto.external.toss.TossErrorRes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(properties =
                "spring.config.location=" +
                "classpath:application.yml" +
                ",classpath:aws.yml")
@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
class JsonUtilsTest {
    @Autowired
    JsonUtils jsonUtils;
    @SpyBean
    ObjectMapper mapper;

    @Test
    @DisplayName("[성공] toJson 테스트")
    public void givenObject_whenToJson_thenReturnStringJson() throws JsonProcessingException {
        //given
        TossErrorRes tossErrorRes = new TossErrorRes("NOT_FOUND", "찾을 수 없습니다.");

        //when
        String toJson = jsonUtils.toJson(tossErrorRes);

        //then
        assertThat(toJson).contains("code", "message");
    }

    @Test
    @DisplayName("[실패] toJson 테스트 - JsonProcessingException 발생 ")
    public void givenObject_whenToJson_thenReturn() throws JsonProcessingException {
        //given
        TossErrorRes tossErrorRes = new TossErrorRes("NOT_FOUND", "찾을 수 없습니다.");
        when(mapper.writeValueAsString(any()))
            .thenThrow(JsonProcessingException.class);

        //when //then
        JsonProcessingException jsonProcessingException = Assertions.assertThrows(JsonProcessingException.class,
                () -> jsonUtils.toJson(tossErrorRes)
        );
        assertThat(jsonProcessingException.getMessage()).isNotNull();
    }

    @Test
    @DisplayName("[성공]fromJson 테스트")
    public void test() throws JsonProcessingException {
        //given
        String code = "NOT_FOUND";
        String message = "찾을 수 없습니다.";
        String json = mapper.writeValueAsString(new TossErrorRes(code, message));

        //when
        TossErrorRes tossErrorRes = jsonUtils.fromJson(json, TossErrorRes.class);

        //then
        assertThat(tossErrorRes)
                .hasFieldOrPropertyWithValue("code", code)
                .hasFieldOrPropertyWithValue("message", message);
    }

}