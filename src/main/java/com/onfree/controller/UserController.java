package com.onfree.controller;

import com.onfree.common.model.SimpleResponse;
import com.onfree.core.dto.UpdateUserNotificationDto;
import com.onfree.core.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Api(value = "사용자 관련 컨트롤러", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserService userService;

    /** 알림설정 */
    @PreAuthorize("isAuthenticated() and @checker.isSelf(#userId)")
    @PutMapping("/api/users/{userId}/notifications")
    public SimpleResponse updateUserNotification(
            @PathVariable("userId")Long userId,
            @Valid @RequestBody UpdateUserNotificationDto updateUserNotificationDto,
            BindingResult errors
    ) {
        userService.updateUserNotification(userId, updateUserNotificationDto);
        return SimpleResponse.success("알림설정이 변경되었습니다.");
    }
}