package com.onfree.controller.requestapply;

import com.onfree.common.annotation.LoginUser;
import com.onfree.common.model.SimpleResponse;
import com.onfree.controller.SwaggerController;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.requestapply.DirectRequestApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class DirectRequestApplyController {
    private final DirectRequestApplyService directRequestApplyService;

    @PostMapping(value = "/api/v1/users/artist/{artistUserId}/apply", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(value = "isAuthenticated()")
    public SimpleResponse directRequestApplyAdd(
            @LoginUser User user,
            @PathVariable Long artistUserId
    ){
        directRequestApplyService.addDirectRequestApply(user, artistUserId);
        SimpleResponse response = SimpleResponse.success("의뢰 신청이 완료되었습니다.");

        response.add(
          linkTo(methodOn(DirectRequestApplyController.class).directRequestApplyAdd(user,artistUserId)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/direct-request-apply-controller/directRequestApplyAddUsingPOST").withRel("profile")
        );
        return response;
    }
}
