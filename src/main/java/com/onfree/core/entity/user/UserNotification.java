package com.onfree.core.entity.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserNotification {
    private Boolean emailRequestNotification;

    private Boolean kakaoRequestNotification;

    private Boolean emailNewsNotification;

    private Boolean kakaoNewsNotification;

    private Boolean pushRequestNotification;

    public static  UserNotification allTrueUserNotification(){
        return UserNotification.builder()
                .emailRequestNotification(true)
                .kakaoRequestNotification(true)
                .emailNewsNotification(true)
                .kakaoNewsNotification(true)
                .pushRequestNotification(true)
                .build();
    }
}
