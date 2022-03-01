package com.onfree.core.entity.user;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Enumerated;
import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum Gender {
    MAN("남성"), WORMAN("여성");
    private final String name;
    public String joinString(){
        return Arrays.stream(Gender.values()).map(String::valueOf).collect(Collectors.joining(","));
    }
}
