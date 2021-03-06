package com.onfree.core.dto.question;

import com.onfree.core.entity.Question;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
@Builder
@Getter
@Relation(collectionRelation = "items")
public class QuestionSimpleDto {
    @ApiModelProperty(value = "질문하기 ID", example = "1L")
    private final Long questionId;

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

    public static QuestionSimpleDto fromEntity(@NonNull final Question question){
        LocalDate createDate = LocalDate.of(1900, 1, 1);
        String createdBy = "null";
        if(question.getCreatedDate() != null){
            createDate = question.getCreatedDate().toLocalDate();
        }
        if(question.getCreatedBy() != null){
            createdBy = question.getCreatedBy();
        }

        return QuestionSimpleDto.builder()
                .questionId(question.getQuestionId())
                .title(question.getTitle())
                .top(question.isTop())
                .view(question.getView())
                .createdDate(createDate)
                .createdBy(createdBy)
                .build();
    }
}
