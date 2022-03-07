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

    @Column(nullable = false)
    private String title; // 프로젝트 제목

    @Column(nullable = false)
    private String content; // 프로젝트 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Setter
    private User user;

    @Column(nullable = false)
    private LocalDate startDate; // 시작 일

    @Column(nullable = false)
    private LocalDate endDate; // 종료 일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UseType useType; // 용도

    @Column(nullable = false)
    private String referenceLink; // 참고 링크

    @Column(nullable = false)
    private Boolean adult; // 성인용 유무

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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

    //== 생성 메소드 ==//
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

    //== 비즈니스 로직 ==//
    public void update( String title, String content, LocalDate startDate,
                       LocalDate endDate, UseType useType, String referenceLink, Boolean adult) {
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.useType = useType;
        this.referenceLink = referenceLink;
        this.adult = adult;
    }

    public void delete() {
        this.status = RequestStatus.REQUEST_DELETED;
    }

    public void finish() {
        this.status = RequestStatus.REQUEST_FINISH;
    }
}



