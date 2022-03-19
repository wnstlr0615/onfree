package com.onfree.core.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageChatDto {
    @NotBlank(message = "message 는 공백일 수 없습니다.")
    private String message;
}
