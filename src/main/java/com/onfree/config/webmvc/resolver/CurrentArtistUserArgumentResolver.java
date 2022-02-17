package com.onfree.config.webmvc.resolver;

import com.onfree.common.annotation.CurrentArtistUser;
import com.onfree.core.entity.user.ArtistUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class CurrentArtistUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentArtistUser.class)
                && parameter.getParameterType().isAssignableFrom(ArtistUser.class);
    }

    @Override
    public ArtistUser resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof ArtistUser){
            return (ArtistUser) authentication.getPrincipal();
        }
        log.info("authentication is null");
        return null;
    }
}
