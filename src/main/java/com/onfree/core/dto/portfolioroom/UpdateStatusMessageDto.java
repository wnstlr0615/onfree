package com.onfree.core.dto.portfolioroom;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class UpdateStatusMessageDto {
    @NotBlank(message = "statusMessage 는 공백일 수 없습니다.")
    private String statusMessage;

    //== 생성 메서드 ==//
    public static UpdateStatusMessageDto createUpdateStatusMessageDto(String message) {
        return UpdateStatusMessageDto.builder()
                .statusMessage(message)
                .build();
    }
}
