package com.onfree.core.dto.notice;

import com.onfree.core.entity.Notice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Getter
@Builder
public class NoticeDetailDto extends RepresentationModel<NoticeDetailDto> {

    @ApiModelProperty(value = "공지글 ID", example = "1L")
    private final Long noticeId;

    @ApiModelProperty(value = "제목", example = "제목")
    private final String title;

    @ApiModelProperty(value = "내용", example = "내용")
    private final String content;

    @ApiModelProperty(value = "조회수", example = "1")
    private final int view;

    @ApiModelProperty(value = "작성일자", example = "2021-01-02")
    private final LocalDate createdDate;

    @ApiModelProperty(value = "작성자", example = "운영자")
    private final String createdBy;

    public static NoticeDetailDto fromEntity(@NonNull Notice notice){
        LocalDate createDate = LocalDate.of(1900, 1, 1);
        String createdBy = "null";
        if(notice.getCreatedDate() != null){
            createDate = notice.getCreatedDate().toLocalDate();
        }
        if(notice.getCreatedBy() != null){
            createdBy = notice.getCreatedBy();
        }
        return NoticeDetailDto.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .view(notice.getView())
                .createdDate(createDate)
                .createdBy(createdBy)
                .build();
    }
}
