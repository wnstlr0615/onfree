package com.onfree.core.dto.portfolioroom;

import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class UpdatePortfolioStatusDto {
    @NotNull(message =  "portfolioStatus  입력은 필수 입니다.")
    private Boolean portfolioStatus;

    //== 생성 메서드 ==//
    public static UpdatePortfolioStatusDto createUpdatePortfolioStatusDto(boolean status) {
        return UpdatePortfolioStatusDto.builder()
                .portfolioStatus(status)
                .build();
    }
}
