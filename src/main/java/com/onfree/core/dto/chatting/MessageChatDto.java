package com.onfree.core.dto.chatting;

import com.onfree.core.entity.chatting.MessageChat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public class MessageChatDto {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    @ApiModel(value = "MessageChatDto_Request")
    public static class Request{

        @ApiModelProperty(value = "전송할 메시지")
        @NotBlank(message = "message 는 공백일 수 없습니다.")
        private String message;
    }

    @ApiModel(value = "MessageChatDto_Response")
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class Response extends RepresentationModel<Response> {
        @ApiModelProperty(value = "메시지", example = "안녕하세요:)")
        private String message;

        public static Response fromEntity(MessageChat messageChat){
            return Response.builder()
                    .message(messageChat.getMessage())
                    .build();
        }
    }
}
