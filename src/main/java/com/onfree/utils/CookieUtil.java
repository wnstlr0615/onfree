package com.onfree.utils;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Component
public class CookieUtil {
    public Cookie createCookie(String name, String value, int expiry){
        final Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expiry);
        cookie.setPath("/");
        return cookie;
    }
    public Cookie resetCookie(String name){
        return createCookie(name, "", 0);
    }

    @Nullable
    public Cookie getCookie(HttpServletRequest request, String name){
        final Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(name)){
                    return cookie;
                }
            }
        }
        return null;
    }
}
