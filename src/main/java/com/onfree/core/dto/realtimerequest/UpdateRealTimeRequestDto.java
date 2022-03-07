package com.onfree.core.dto.realtimerequest;

import com.onfree.core.entity.realtimerequset.UseType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class UpdateRealTimeRequestDto {
    @NotBlank(message = "title은 필수 입니다.")
    @Size(min = 5, message = "title 는 5자 이상이여야 합니다.")
    @ApiModelProperty(value = "실시간 의뢰 제목", example = " 실시간 의뢰 제목")
    private String title; // 프로젝트 제목

    @NotBlank(message = "content 는 필수 입니다.")
    @ApiModelProperty(value = "실시간 의뢰 내용", example = "실시간 의뢰 내용")
    private String content; // 프로젝트 내용

    @NotNull(message = "startDate 는 필수 입니다.")
    @ApiModelProperty(value = "프로젝트 시작 일", example = "2022-03-07", dataType = "LocalDate")
    private LocalDate startDate; // 시작 일

    @FutureOrPresent(message = "endDate는 현재보다 과거 일 수 업습니다.")
    @NotNull(message = "endDate 는 필수 입니다.")
    @ApiModelProperty(value = "프로젝트 마감 일", example = "2022-03-08", dataType = "LocalDate")
    private LocalDate endDate; // 종료 일

    @NotNull(message = "useType 는 필수 입니다.")
    @ApiModelProperty(value = "의뢰 디자인 용도", example = "COMMERCIAL", allowableValues = "COMMERCIAL,NOT_COMMERCIAL")
    private UseType useType; // 용도

    @ApiModelProperty(value = "참고 링크", example = "http://naver.com")
    private String referenceLink; // 참고 링크

    @NotNull(message = "adult 는 필수 입니다.")
    @ApiModelProperty(value = "성인물 유무", example = "false")
    private Boolean adult; // 성인용 유무


    //== 생성 메서드 ==//
    public static UpdateRealTimeRequestDto createUpdateRealTimeRequestDto(String title, String content, LocalDate startDate, LocalDate endDate, UseType useType, String referenceLink, boolean adult){
        return UpdateRealTimeRequestDto.builder()
                .title(title)
                .content(content)
                .startDate(startDate)
                .endDate(endDate)
                .useType(useType)
                .referenceLink(referenceLink)
                .adult(adult)
                .build();
    }
}
