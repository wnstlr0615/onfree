package com.onfree.core.dto.question;

import com.onfree.core.entity.Question;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDate;

@Builder
@Getter
@ApiModel(value = "QuestionDetailDto", description = "자주하기 질문 상세 정보")
public class QuestionDetailDto {
    @ApiModelProperty(value = "질문하기 ID", example = "1L")
    private final Long questionId;

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

public static QuestionDetailDto fromEntity(@NonNull Question question){
        return QuestionDetailDto.builder()
                .questionId(question.getQuestionId())
                .title(question.getTitle())
                .content(question.getContent())
                .view(question.getView())
                .createdDate(question.getCreatedDate().toLocalDate())
                .createdBy(question.getCreatedBy())
                .build();
    }
}
