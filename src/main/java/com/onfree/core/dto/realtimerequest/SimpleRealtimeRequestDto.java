package com.onfree.core.dto.realtimerequest;

import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Relation(collectionRelation = "items")
public class SimpleRealtimeRequestDto extends RepresentationModel<SimpleRealtimeRequestDto> {
    @ApiModelProperty(value = "실시간 의뢰 Pk", example = "1L")
    private Long realTimeRequestId;

    @ApiModelProperty(value = "실시간 의뢰 제목", example = " 실시간 의뢰 제목")
    private String title; // 프로젝트 제목

    @ApiModelProperty(value = "실시간 의뢰 요청자 닉네임", example = "닉네임")
    private String nickname;

    @ApiModelProperty(value = "실시간 의뢰 상태", example = "모집중", allowableValues = "모집중,마감,의뢰중,삭제됨")
    private String status;

    @ApiModelProperty(value = "프로젝트 시작 일", example = "2022-03-07", dataType = "LocalDate")
    private LocalDate startDate;

    @ApiModelProperty(value = "프로젝트 마감 일", example = "2022-03-08", dataType = "LocalDate")
    private LocalDate endDate;

    @ApiModelProperty(value = "실시간 의뢰 생성 날짜", example = "2022-03-07", dataType = "LocalDate")
    private LocalDate createDate;


    /** 생성 메소드 */
    public static SimpleRealtimeRequestDto fromEntity(RealTimeRequest realTimeRequest) {
        return SimpleRealtimeRequestDto.builder()
                .realTimeRequestId(realTimeRequest.getRealTimeRequestId())
                .title(realTimeRequest.getTitle())
                .nickname(realTimeRequest.getUser().getNickname())
                .status(realTimeRequest.getStatus().getDisplayStatus())
                .startDate(realTimeRequest.getStartDate())
                .endDate(realTimeRequest.getEndDate())
                .createDate(realTimeRequest.getCreatedDate().toLocalDate())
                .build();
    }



    public static SimpleRealtimeRequestDto createSimpleRealtimeRequestDto(
            Long realTimeRequestId, String title, String nickname, String status,
            LocalDate startDate, LocalDate endDate, LocalDate createDate
    ) {
        return SimpleRealtimeRequestDto.builder()
                .realTimeRequestId(realTimeRequestId)
                .title(title)
                .nickname(nickname)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .createDate(createDate)
                .build();
    }
}
