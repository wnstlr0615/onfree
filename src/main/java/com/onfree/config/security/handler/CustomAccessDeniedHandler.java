package com.onfree.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.response.SimpleErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.setStatus(403);
        response.getWriter().write(
                mapper.writeValueAsString(
                        errorResponse()
                )
        );
    }

    public SimpleErrorResponse errorResponse(){
        return SimpleErrorResponse.fail(GlobalErrorCode.ACCESS_DENIED);
    }
}
