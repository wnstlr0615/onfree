package com.onfree.controller;

import com.onfree.common.annotation.LoginUser;
import com.onfree.common.model.SimpleResponse;
import com.onfree.core.dto.user.UpdateUserNotificationDto;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaTypes.HAL_JSON_VALUE)
public class UserController {
    private final UserService userService;

    /** 알림설정 */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me/notifications")
    public SimpleResponse userNotificationModify(
            @LoginUser User user,
            @Valid @RequestBody UpdateUserNotificationDto updateUserNotificationDto,
            BindingResult errors
    ) {
        userService.updateUserNotification(user.getUserId(), updateUserNotificationDto);
        SimpleResponse response = SimpleResponse.success("알림설정이 변경되었습니다.");

        //링크 추가
        response.add(
                linkTo(UserController.class).slash("me").slash("notifications").withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/user-controller/userNotificationModifyUsingPUT").withRel("profile")
        );
        return response;
    }
}
