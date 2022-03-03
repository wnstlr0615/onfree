package com.onfree.core.entity.realtimerequset;

import com.onfree.common.model.BaseTimeEntity;
import com.onfree.core.entity.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RealTimeRequest extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long realTimeRequestId;

    private String title; // 프로젝트 제목

    private String content; // 프로젝트 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate startDate; // 시작 일

    private LocalDate endDate; // 종료 일

    @Enumerated(EnumType.STRING)
    private UseType useType; // 용도

    private String referenceLink; // 참고 링크

    private Boolean adult; // 성인용 유무

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // 실시간 의뢰 상태


    @Builder
    public RealTimeRequest(LocalDateTime createdDate, LocalDateTime updatedDate, Long realTimeRequestId, String title, String content, User user, LocalDate startDate, LocalDate endDate, UseType useType, String referenceLink, Boolean adult, RequestStatus status) {
        super(createdDate, updatedDate);
        this.realTimeRequestId = realTimeRequestId;
        this.title = title;
        this.content = content;
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.useType = useType;
        this.referenceLink = referenceLink;
        this.adult = adult;
        this.status = status;
    }

    /** 생성 메소드 */
    public static RealTimeRequest createRealTimeRequest(
            Long realTimeRequestId, String title, String content, User user, LocalDate startDate,
            LocalDate endDate, UseType useType, String referenceLink, Boolean adult, RequestStatus status, LocalDateTime createdDate
    ){
        return RealTimeRequest.builder()
                .realTimeRequestId(realTimeRequestId)
                .title(title)
                .content(content)
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .useType(useType)
                .referenceLink(referenceLink)
                .adult(adult)
                .status(status)
                .createdDate(createdDate)
                .build();



    }



}



