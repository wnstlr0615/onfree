package com.onfree.config.security.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtLoginResponse {
    @ApiModelProperty(value = "로그인 결과", notes = "로그인 성공 시 true . flag 용 변수   필요 없을 경우 삭제 예정", example = "true")
    private final boolean result;

    @ApiModelProperty(value = "AccessToken", notes = "토큰 만료 시간 30 분, 쿠키에도 저장됨")
    private final String accessToken;

    @ApiModelProperty(value = "RefreshToken", notes = "토큰 만료 일 7일 , 쿠키에도 저장됨")
    private final String refreshToken;

    @ApiModelProperty(value = "사용자 email", notes = "필요 없을 경우 삭제 예정", example = "joon@naver.com")
    private final String username;
}
