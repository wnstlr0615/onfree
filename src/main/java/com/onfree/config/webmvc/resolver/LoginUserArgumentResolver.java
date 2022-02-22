package com.onfree.config.webmvc.resolver;

import com.onfree.common.annotation.LoginUser;
import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.exception.GlobalException;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.entity.user.NormalUser;
import com.onfree.core.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class)
                || parameter.getParameterType().isAssignableFrom(User.class);
    }

    @Override
    public User resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            Object principal = authentication.getPrincipal();
            if(principal instanceof ArtistUser){
                return (ArtistUser) principal;
            }
            if(principal instanceof NormalUser){
                return (NormalUser) principal;
            }
        }
        log.error("authentication is null");
        throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }
}
