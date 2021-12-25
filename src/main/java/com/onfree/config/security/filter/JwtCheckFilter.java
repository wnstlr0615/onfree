package com.onfree.config.security.filter;

import com.onfree.config.error.handler.CustomAuthenticationEntryPoint;
import com.onfree.config.security.CustomUserDetail;
import com.onfree.config.security.CustomUserDetailService;
import com.onfree.core.model.VerifyResult;
import com.onfree.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
public class JwtCheckFilter extends OncePerRequestFilter {
    public static final String BEARER = "Bearer";
    private final CustomUserDetailService userDetailService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    public JwtCheckFilter( CustomUserDetailService customUserDetailsService, CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.userDetailService = customUserDetailsService;
        this.authenticationEntryPoint=authenticationEntryPoint;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(header) || !header.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(BEARER.length()).trim();

        try {
            final VerifyResult verify = JWTUtil.verify(token);
            if (verify != null && verify.isResult()) {
                final CustomUserDetail customUserDetail = (CustomUserDetail) userDetailService.loadUserByUsername(verify.getUsername());
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(customUserDetail.getUser(), null, customUserDetail.getAuthorities()));
            }
        }catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request,response,e);
            return;
        }
        filterChain.doFilter(request,response);
    }
}
