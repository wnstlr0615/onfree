package com.onfree.controller.chatting;

import com.onfree.common.annotation.LoginUser;
import com.onfree.core.dto.chatting.MessageChatDto;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.chatting.ChattingService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ChattingController {

    private final ChattingService chattingService;

    @ApiOperation(value = "채팅방 메시지 보내기")
    @PreAuthorize(value = "isAuthenticated()")
    @MessageMapping("/chatting/{roomId}/message")
    public void messageChatAdd(
            @DestinationVariable(value = "roomId")Long roomId,
            @LoginUser User user,
            @Valid @RequestBody MessageChatDto.Request messageChatDto
    ){
        chattingService.AddMessageChat(roomId, user, messageChatDto);
    }

    @MessageMapping("/chatting/{roomId}/test")
    public void messageChatAdd(
            @DestinationVariable(value = "roomId")Long roomId,
            @Valid @RequestBody MessageChatDto.Request messageChatDto
    ){
        chattingService.test(roomId, messageChatDto);
    }


}
