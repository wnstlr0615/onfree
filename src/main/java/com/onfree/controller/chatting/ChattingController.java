package com.onfree.controller.chatting;

import com.onfree.common.annotation.CurrentArtistUser;
import com.onfree.common.annotation.LoginUser;
import com.onfree.core.dto.chatting.ChatDto;
import com.onfree.core.dto.chatting.EstimateSheetChatDto;
import com.onfree.core.dto.chatting.MessageChatDto;
import com.onfree.core.dto.external.toss.refund.RefundRequestDto;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.chatting.ChattingService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ChattingController {

    private final ChattingService chattingService;

    @ApiOperation(value = "채팅방 대화 불러오기")
    @GetMapping("/chatting/{roomId}")
    @PreAuthorize(value = "isAuthenticated()")
    public List<ChatDto> chatList(
            @PathVariable Long roomId,
            @LoginUser User user
    ){
        return chattingService.findAllChat(roomId, user);
    }

    @ApiOperation(value = "채팅방 메시지 보내기")
    @PreAuthorize(value = "isAuthenticated()")
    @MessageMapping("/chatting/{roomId}/message")
    public void messageChatAdd(
            @DestinationVariable(value = "roomId")Long roomId,
            @LoginUser User user,
            @Valid @RequestBody MessageChatDto.Request messageChatDto
    ){
        chattingService.addMessage(roomId, user, messageChatDto);
    }

    @ApiOperation(value = "채팅방 견적서 보내기")
    @PreAuthorize(value = "isAuthenticated()")
    @MessageMapping("/chatting/{roomId}/estimate")
    public void estimateSheetChatAdd(
            @DestinationVariable(value = "roomId") Long roomId,
            @CurrentArtistUser ArtistUser artistUser,
            @Valid EstimateSheetChatDto.Request request
    ){
       chattingService.addEstimateSheet(roomId, artistUser, request);
    }

    @ApiOperation(value = " 지급 요청 하기")
    @PreAuthorize(value = "hasRole('ARTIST')")
    @MessageMapping("/chatting/{roomId}/payment-request")
    public void paymentRequest(
            @DestinationVariable(value = "roomId") Long roomId,
            @LoginUser User sender
    ) {
        chattingService.requestPayment(roomId, sender);
    }

    @ApiOperation(value = " 지급 요청 수락 하기")
    @PreAuthorize(value = "isAuthenticated()")
    @MessageMapping("/chatting/{roomId}/payment-request/accept")
    public void paymentRequestAccept(
            @DestinationVariable(value = "roomId") Long roomId,
            @LoginUser User sender
    ){
        chattingService.acceptPaymentRequest(roomId, sender);
    }

    @ApiOperation(value = "환불 요청하기")
    @PreAuthorize(value = "isAuthenticated()")
    @MessageMapping("/chatting/{roomId}/refund-request")
    public void refundRequest(
            @DestinationVariable(value = "roomId") Long roomId,
            @LoginUser User sender
    ){
        chattingService.requestRefund(roomId, sender);
    }


    @ApiOperation(value = "환불 요청 확인 하기")
    @PreAuthorize(value = "hasRole('ARTIST')")
    @MessageMapping("/chatting/{roomId}/refund-request/accept")
    public void refundRequestAccept(
            @PathVariable Long applyId,
            @LoginUser User sender,
            @Valid @RequestBody RefundRequestDto refundRequestDto
    ){
        chattingService.acceptRequestRefund(applyId, sender, refundRequestDto);
    }

}
