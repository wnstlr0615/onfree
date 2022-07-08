package com.onfree.controller.user;

import com.onfree.common.annotation.LoginUser;
import com.onfree.common.model.SimpleResponse;
import com.onfree.controller.SwaggerController;
import com.onfree.core.dto.user.UpdateUserNotificationDto;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users")
public class UserController {
    private final UserService userService;

    /** 알림설정 */
    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/me/notifications", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse userNotificationModify(
            @LoginUser User user,
            @Valid @RequestBody UpdateUserNotificationDto updateUserNotificationDto
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
