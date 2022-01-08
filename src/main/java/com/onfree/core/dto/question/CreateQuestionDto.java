package com.onfree.core.dto.question;


import com.onfree.core.entity.Question;
import com.onfree.core.entity.Question;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CreateQuestionDto {
    @ApiModel("CreateQuestionDto_Request")
    @Getter
    @Builder
    public static class Request{
        @NotBlank(message = "제목은 공백일 수 없습니다.")
        @Length(min = 5,max = 100, message = "제목의 길이는 5 ~ 100 입니다.")
        @ApiModelProperty(value = "제목", example = "온프리 서비스 오픈 ")
        private String title;

        @NotBlank(message = "내용이 공백일 수 는 없습니다.")
        @ApiModelProperty(value = "내용", example = "온프리 서비스 오픈 했습니다. ")
        private String content;

        @NotNull(message = "상단 설정 값이 null 입니다. ")
        @ApiModelProperty(value = "상단 설정", example = "true")
        private Boolean top;

        public Question toEntity(){
            return Question.builder()
                    .title(title)
                    .content(content)
                    .top(top)
                    .disabled(false)
                    .view(0)
                    .build();
        }
    }

    @ApiModel("CreateQuestionDto_Response")
    @Getter
    @Builder
    public static class Response{
        @ApiModelProperty(value = "질문 번호", example = "1")
        private Long questionId;
        
        @ApiModelProperty(value = "제목", example = "온프리 서비스 오픈 ")
        private String title;

        @ApiModelProperty(value = "내용", example = "온프리 서비스 오픈 했습니다. ")
        private String content;

        @ApiModelProperty(value = "상단 설정", example = "true")
        private Boolean top;

        @ApiModelProperty(value = "삭제 설정", example = "false")
        private Boolean disabled;

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
