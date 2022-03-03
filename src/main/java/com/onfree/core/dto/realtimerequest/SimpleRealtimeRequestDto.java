package com.onfree.core.dto.realtimerequest;

import com.onfree.core.entity.realtimerequset.RealTimeRequest;
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
    private Long realTimeRequestId;

    private String title; // 프로젝트 제목

    private String nickname;

    private String status;

    private LocalDate startDate;

    private LocalDate endDate;

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
