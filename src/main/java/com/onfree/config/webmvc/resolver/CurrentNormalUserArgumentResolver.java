package com.onfree.config.webmvc.resolver;

import com.onfree.common.annotation.CurrentArtistUser;
import com.onfree.common.annotation.CurrentNormalUser;
import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.exception.GlobalException;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.entity.user.NormalUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class CurrentNormalUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentNormalUser.class)
                && parameter.getParameterType().isAssignableFrom(NormalUser.class);
    }

    @Override
    public NormalUser resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof NormalUser){
            return (NormalUser) authentication.getPrincipal();
        }
        log.error("authentication is not normal user");
        throw new GlobalException(GlobalErrorCode.ACCESS_DENIED);
    }
}
