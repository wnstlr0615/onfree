package com.onfree.core.dto;

import com.onfree.core.entity.user.UserNotification;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
public class UpdateUserNotificationDto {
    @NotNull(message = "해당 변수는 null일 수 없습니다.")
    @ApiModelProperty(value = "이메일 의뢰하기 알림 여부", example = "true", allowableValues = "false,true")
    private final Boolean emailRequestNotification;

    @ApiModelProperty(value = "카카오 의뢰하기 알림 여부", example = "true", allowableValues = "false,true")
    @NotNull(message = "해당 변수는 null일 수 없습니다.")
    private final Boolean kakaoRequestNotification;

    @ApiModelProperty(value = "이메일로 최신 뉴스 알림 여부", example = "true", allowableValues = "false,true")
    @NotNull(message = "해당 변수는 null일 수 없습니다.")
    private final Boolean emailNewsNotification;

    @ApiModelProperty(value = "카카오로 최신 뉴스  알림 여부", example = "true", allowableValues = "false,true")
    @NotNull(message = "해당 변수는 null일 수 없습니다.")
    private final Boolean kakaoNewsNotification;

    @ApiModelProperty(value = "의뢰하기 PUSH 알림 여부", example = "true", allowableValues = "false,true")
    @NotNull(message = "해당 변수는 null일 수 없습니다.")
    private final Boolean pushRequestNotification;

    public  UserNotification toEntity(){
        return UserNotification.builder()
                .kakaoRequestNotification(kakaoRequestNotification)
                .emailRequestNotification(emailRequestNotification)
                .kakaoNewsNotification(kakaoNewsNotification)
                .emailNewsNotification(emailNewsNotification)
                .pushRequestNotification(pushRequestNotification)
                .build();
    }
}
