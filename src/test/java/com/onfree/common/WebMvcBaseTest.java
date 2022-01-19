package com.onfree.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.config.security.CustomUserDetailService;
import com.onfree.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest
public abstract class WebMvcBaseTest {
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockBean
    protected CustomUserDetailService customUserDetailService;

    @MockBean
    protected JWTUtil jwtUtil;
}
