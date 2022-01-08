package com.onfree.core.dto.question;

import com.onfree.core.entity.Question;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UpdateQuestionDto {
    @ApiModel(value = "UpdateQuestion_Request")
    @Getter
    @Builder
    public static class Request{
        @NotBlank(message = "제목은 공백일 수 없습니다.")
        @Length(min = 5,max = 100, message = "제목의 길이는 5 ~ 100 입니다.")
        @ApiModelProperty(value = "제목", example = "온프리 서비스 오픈 ")
        private final String title;

        @NotBlank(message = "내용이 공백일 수 는 없습니다.")
        @ApiModelProperty(value = "내용", example = "온프리 서비스 오픈 했습니다. ")
        private final String content;

        @NotNull(message = "상단 설정 값이 null 입니다. ")
        @ApiModelProperty(value = "상단 설정", example = "true")
        private final Boolean top;

        @NotNull(message = "삭제 설정 값이 null 입니다. ")
        @ApiModelProperty(value = "삭제 설정", example = "false")
        private final Boolean disabled;


    }
    @ApiModel(value = "UpdateQuestion_Response")
    @Getter
    @Builder
    public static class Response{

        @ApiModelProperty(value = "질문 번호", example = "1")
        private final Long questionId;

        @ApiModelProperty(value = "제목", example = "온프리 서비스 오픈 ")
        private final String title;

        @ApiModelProperty(value = "내용", example = "온프리 서비스 오픈 했습니다. ")
        private final String content;

        @ApiModelProperty(value = "상단 설정", example = "true")
        private final Boolean top;

        @ApiModelProperty(value = "삭제 설정", example = "false")
        private final Boolean disabled;

        public static Response fromEntity(Question question) {
            return Response.builder()
                    .questionId(question.getQuestionId())
                    .title(question.getTitle())
                    .content(question.getContent())
                    .top(question.isTop())
                    .disabled(question.isDisabled())
                    .build();
        }
    }
}
