package com.onfree.controller;

import com.onfree.common.annotation.LoginUser;
import com.onfree.common.model.SimpleResponse;
import com.onfree.core.dto.realtimerequest.SimpleRealtimeRequestDto;
import com.onfree.core.dto.user.UpdateUserNotificationDto;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.RealTimeRequestService;
import com.onfree.core.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
