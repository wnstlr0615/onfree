package com.onfree.controller.chatting;

import com.onfree.common.annotation.LoginUser;
import com.onfree.common.model.SimpleResponse;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.chatting.DirectRequestChattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DirectRequestChattingController {
    private final DirectRequestChattingService directRequestChattingService;


    @PostMapping(value = "/api/v1/applications/{applyId}/chatting/{receiverId}")
    @PreAuthorize(value = "isAuthenticated()")
    public SimpleResponse chattingAdd(
            @PathVariable Long applyId,
            @PathVariable Long receiverId,
            @LoginUser User sender,
            @RequestParam String message
    ){
        directRequestChattingService.addChatting(applyId, sender, receiverId, message);
        return SimpleResponse.success("메시지 전송이 완료되었습니다.");
    }
}
