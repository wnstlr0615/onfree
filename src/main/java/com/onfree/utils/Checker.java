package com.onfree.utils;

import com.onfree.core.entity.user.User;
import com.onfree.error.code.GlobalErrorCode;
import com.onfree.error.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class Checker {
    public boolean isSelf(Long userId){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final User user = (User) authentication.getPrincipal();
        log.info("checker - userId = {}", user.getUserId());
        if(!isEqualsAccessUserId(user, userId)){
            log.error("REQUEST_USER_Id is not matching USER_ID");
            log.error("request_user_id : {}, user_id : {}", userId, user.getUserId());
            throw new GlobalException(GlobalErrorCode.ACCESS_DENIED);
        }
        return true;
    }

    private boolean isEqualsAccessUserId(User user, Long userId) {
        return userId.equals(user.getUserId());
    }
}
