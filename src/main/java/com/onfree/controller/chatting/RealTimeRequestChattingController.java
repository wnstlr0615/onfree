package com.onfree.controller.chatting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.common.annotation.LoginUser;
import com.onfree.common.model.SimpleResponse;
import com.onfree.controller.SwaggerController;
import com.onfree.core.dto.chatting.EstimateSheetChatDto;
import com.onfree.core.dto.chatting.MessageChatDto;
import com.onfree.core.dto.external.toss.refund.RefundRequestDto;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.chatting.RealTimeRequestChattingService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class RealTimeRequestChattingController {
    private final RealTimeRequestChattingService chattingService;
    private  RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper;

    @ApiOperation(value = "실시간 의뢰를 통한 채팅시 메시지 보내기")
    @PreAuthorize(value = "isAuthenticated()")
    @PostMapping(value = "/api/v1/applications/{applyId}/chatting/message")
    public ResponseEntity<?> messageChatAdd(
            @PathVariable Long applyId,
            @LoginUser User sender,
            @Valid @RequestBody MessageChatDto.Request messageChatDto,
            BindingResult errors
    ) {

        MessageChatDto.Response response = chattingService.addMessage(applyId, sender, messageChatDto);

        response.add(
          linkTo(
                  methodOn(RealTimeRequestChattingController.class).messageChatAdd(applyId, sender, messageChatDto, errors)
          ).withSelfRel(),
            Link.of(linkTo(SwaggerController.class) + "/#/real-time-request-chatting-controller/messageChatAddUsingPOST").withRel("profile")
        );
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "실시간 의뢰를 통한 견적서 보내기")
    @PreAuthorize(value = "hasRole('ARTIST')")
    @PostMapping(value = "/api/v1/applications/{applyId}/chatting/estimate")
    public ResponseEntity<?> estimateSheetAdd(
            @PathVariable Long applyId,
            @LoginUser User sender,
            @Valid @RequestBody EstimateSheetChatDto.Request chatDto,
            BindingResult errors
    ) {

        EstimateSheetChatDto.Response response = chattingService.addEstimateSheet(applyId, sender, chatDto);

        response.add(
                linkTo(
                        methodOn(RealTimeRequestChattingController.class).estimateSheetAdd(applyId, sender, chatDto, errors)
                ).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/real-time-request-chatting-controller/estimateSheetAddUsingPOST").withRel("profile")
        );
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = " 지급 요청 하기")
    @PreAuthorize(value = "hasRole('ARTIST')")
    @PutMapping(value = "/api/v1/applications/{applyId}/payment-request")
    public SimpleResponse paymentRequest(
            @PathVariable Long applyId,
            @LoginUser User sender
    ) {
        chattingService.requestPayment(applyId, sender);
        return SimpleResponse.success("지급 요청이 완료되었습니다.");
    }

    @ApiOperation(value = " 지급 요청 수락 하기")
    @PreAuthorize(value = "isAuthenticated()")
    @PutMapping(value = "/api/v1/applications/{applyId}/payment-request/accept")
    public SimpleResponse paymentRequestAccept(
            @PathVariable Long applyId,
            @LoginUser User sender
    ){
        chattingService.acceptPaymentRequest(applyId, sender);
        return SimpleResponse.success("지급 요청을 수락하였습니다.");
    }

    @ApiOperation(value = "환불 요청하기")
    @PreAuthorize(value = "isAuthenticated()")
    @PutMapping("/api/v1/applications/{applyId}/refund-request")
    public SimpleResponse refundRequest(
            @PathVariable Long applyId,
            @LoginUser User sender
            ){
        chattingService.requestRefund(applyId, sender);
        return SimpleResponse.success("환불요청이 완료되었습니다.");
    }


    @ApiOperation(value = "환불 요청 확인 하기")
    @PreAuthorize(value = "hasRole('ARTIST')")
    @PutMapping("/api/v1/applications/{applyId}/refund-request/accept")
    public SimpleResponse refundRequestAccept(
            @PathVariable Long applyId,
            @LoginUser User sender,
            @Valid @RequestBody RefundRequestDto refundRequestDto,
            BindingResult errors
    ){
        chattingService.acceptRequestRefund(applyId, sender, refundRequestDto);
        return SimpleResponse.success("환불요청이 완료되었습니다.");
    }



}



