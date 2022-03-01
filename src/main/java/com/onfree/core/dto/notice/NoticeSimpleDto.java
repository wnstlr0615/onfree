package com.onfree.core.dto.notice;

import com.onfree.core.entity.Notice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;

@Getter
@Builder
@Relation(collectionRelation = "items")
public class NoticeSimpleDto {

    @ApiModelProperty(value = "공지글 ID", example = "1L")
    private final Long noticeId;

    @ApiModelProperty(value = "제목", example = "제목")
    private final String title;

    @ApiModelProperty(value = "상단 설정", example = "false")
    private final boolean top;

    @ApiModelProperty(value = "조회수", example = "1")
    private final int view;

    @ApiModelProperty(value = "작성일자", example = "2021-01-02")
    private final LocalDate createdDate;

    @ApiModelProperty(value = "작성자", example = "운영자")
    private final String createdBy;

    public static NoticeSimpleDto fromEntity(@NonNull final Notice notice){
        LocalDate createDate = LocalDate.of(1900, 1, 1);
        String createdBy = "null";
        if(notice.getCreatedDate() != null){
            createDate = notice.getCreatedDate().toLocalDate();
        }
        if(notice.getCreatedBy() != null){
            createdBy = notice.getCreatedBy();
        }
        return NoticeSimpleDto.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .top(notice.isTop())
                .view(notice.getView())
                .createdDate(createDate)
                .createdBy(createdBy)
                .build();
    }
}
