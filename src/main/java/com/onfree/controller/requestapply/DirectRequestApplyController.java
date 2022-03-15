package com.onfree.controller.requestapply;

import com.onfree.common.annotation.LoginUser;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.requestapply.DirectRequestApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DirectRequestApplyController {
    private final DirectRequestApplyService directRequestApplyService;

    @PostMapping(value = "/api/v1/users/artist/{artistUserId}/apply", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(value = "isAuthenticated()")
    public void directRequestApplyAdd(
            @LoginUser User user,
            @PathVariable Long artistUserId
    ){
        directRequestApplyService.addDirectRequestApply(user, artistUserId);
    }
}
