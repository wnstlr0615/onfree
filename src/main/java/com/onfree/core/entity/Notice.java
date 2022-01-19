package com.onfree.core.entity;

import com.onfree.core.dto.notice.UpdateNoticeDto;
import com.onfree.common.model.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class Notice extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private boolean top;

    @Column(nullable = false)
    private boolean disabled;

    @Column(nullable = false)
    private int view;

    public void updateView() {
        view=view + 1;
    }

    public void updateByUpdateNoticeDto(UpdateNoticeDto.Request request) {
        title = request.getTitle() != null ? request.getTitle() : title;
        content = request.getContent() != null ? request.getContent() : content;
        top = request.getTop() != null ? request.getTop() : top;
        disabled = request.getDisabled() != null ? request.getDisabled() : disabled;
    }
}
