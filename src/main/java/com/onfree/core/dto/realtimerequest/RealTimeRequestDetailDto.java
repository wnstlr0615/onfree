package com.onfree.core.dto.realtimerequest;

import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import com.onfree.core.entity.realtimerequset.RequestStatus;
import com.onfree.core.entity.realtimerequset.UseType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RealTimeRequestDetailDto extends RepresentationModel<RealTimeRequestDetailDto> {
    @ApiModelProperty(value = "실시간 의뢰 제목", example = " 실시간 의뢰 제목")
    private Long realTimeRequestId;

    @ApiModelProperty(value = "실시간 의뢰 제목", example = " 실시간 의뢰 제목")
    private String title; // 프로젝트 제목

    @ApiModelProperty(value = "실시간 의뢰 내용", example = "실시간 의뢰 내용")
    private String content; // 프로젝트 내용

    @ApiModelProperty(value = "실시간 의뢰 요청자 닉네임", example = "닉네임")
    private String nickname;

    @ApiModelProperty(value = "프로젝트 시작 일", example = "2022-03-07", dataType = "LocalDate")
    private LocalDate startDate; // 시작 일

    @ApiModelProperty(value = "프로젝트 마감 일", example = "2022-03-08", dataType = "LocalDate")
    private LocalDate endDate; // 종료 일

    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "의뢰 디자인 용도", example = "COMMERCIAL", allowableValues = "COMMERCIAL,NOT_COMMERCIAL")
    private UseType useType; // 용도

    @ApiModelProperty(value = "참고 링크", example = "http://naver.com")
    private String referenceLink; // 참고 링크

    @ApiModelProperty(value = "성인물 유무", example = "false")
    private Boolean adult; // 성인용 유무

    @ApiModelProperty(value = "실시간 의뢰 상태", example = "REQUEST_RECRUITING", allowableValues = "REQUEST_RECRUITING,REQUEST_REQUESTING,REQUEST_FINISH,REQUEST_DELETED")
    private RequestStatus status; // 실시간 의뢰 상태
    
    private LocalDate createDate;

    public static RealTimeRequestDetailDto createRealTimeRequestDetail(
            Long realTimeRequestId, String title, String content, String nickname, LocalDate startDate, LocalDate endDate, 
            UseType useType, String referenceLink, Boolean adult, RequestStatus status, LocalDate createDate) {
         return RealTimeRequestDetailDto.builder()
                 .realTimeRequestId(realTimeRequestId)
                 .title(title)
                 .content(content)
                 .nickname(nickname)
                 .startDate(startDate)
                 .endDate(endDate)
                 .useType(useType)
                 .referenceLink(referenceLink)
                 .adult(adult)
                 .status(status)
                 .createDate(createDate)
                 .build();
    }

    public static RealTimeRequestDetailDto fromEntity(RealTimeRequest realTimeRequest) {
        return RealTimeRequestDetailDto.builder()
                .realTimeRequestId(realTimeRequest.getRealTimeRequestId())
                .title(realTimeRequest.getTitle())
                .content(realTimeRequest.getContent())
                .nickname(realTimeRequest.getUser().getNickname())
                .startDate(realTimeRequest.getStartDate())
                .endDate(realTimeRequest.getEndDate())
                .useType(realTimeRequest.getUseType())
                .referenceLink(realTimeRequest.getReferenceLink())
                .adult(realTimeRequest.getAdult())
                .status(realTimeRequest.getStatus())
                .createDate(realTimeRequest.getCreatedDate().toLocalDate())
                .build();
        }
}
