package com.onfree.utils;

import com.onfree.core.entity.user.User;
import lombok.experimental.UtilityClass;
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
        return isEqualsAccessUserId(user, userId);
    }

    private boolean isEqualsAccessUserId(User user, Long userId) {
        return userId.equals(user.getUserId());
    }
}
