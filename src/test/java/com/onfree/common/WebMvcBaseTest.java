package com.onfree.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.config.security.CustomUserDetailService;
import com.onfree.core.service.JWTRefreshTokenService;
import com.onfree.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
public abstract class WebMvcBaseTest {
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockBean
    protected CustomUserDetailService customUserDetailService;

    @MockBean
    protected JWTRefreshTokenService jwtRefreshTokenService;

    @MockBean
    protected JWTUtil jwtUtil;
}
