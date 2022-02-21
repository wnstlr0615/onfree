package com.onfree.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.config.security.filter.JwtCheckFilter;
import com.onfree.config.security.filter.JwtLoginFilter;
import com.onfree.config.security.handler.CustomAccessDeniedHandler;
import com.onfree.config.security.handler.CustomAuthenticationEntryPoint;
import com.onfree.config.security.handler.JwtLoginAuthenticationFailHandler;
import com.onfree.core.service.LoginService;
import com.onfree.utils.CookieUtil;
import com.onfree.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
@Order()
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Qualifier("loginService")
    @Autowired(required = false)
    private LoginService loginService;

    @Qualifier("cookieUtil")
    @Autowired(required = false)
    private CookieUtil cookieUtil;

    private final CustomUserDetailService customUserDetailService;
    private final JWTUtil jwtUtil;
    private final ObjectMapper mapper;

    @Override
    public void configure(WebSecurity web){
        web.ignoring()
                .antMatchers("/v3/api-docs", "/swagger-resources/configuration/ui",
                        "/swagger-resources", "/swagger-resources/configuration/security",
                        "/swagger-ui/", "/webjars/**","/swagger-ui/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailService)
                .passwordEncoder(passwordEncoder());

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] whiteList=new String[]{
                "/login", "/error", "/logout", "/api/signup/**", "/api/password/reset/**",
        };
        String[] getWhiteList = new String[]{
                "/api/v1/notices/**",  "/api/v1/questions/**",
                "/api/portfolios/**", "/api/v1/users/artist/**"
        };
        String[] postWhiteList = new String[]{
                "/api/upload/profile-image",
        };
        String[] onlyArtistUrl = new String[]{
                "/api/v1/users/artist/**",
                "/api/upload/profile-image", "/api/upload/portfolio-content-image"
        };
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST,"/api/v1/users/artist", "/api/users/normal").permitAll()
                .antMatchers(whiteList).permitAll()
                .antMatchers(HttpMethod.GET, getWhiteList).permitAll()
                .antMatchers(HttpMethod.POST, postWhiteList).permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/notices/**", "/api/v1/questions/**").hasRole("ADMIN")
                .antMatchers(onlyArtistUrl).hasRole("ARTIST")
                .antMatchers("/api/users/normal/**").hasRole("NORMAL")
                .anyRequest().authenticated();
        http.httpBasic().disable()
            .csrf().disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedException())
        ;

        http
                .addFilterAt(
                        new JwtCheckFilter(customUserDetailService, authenticationEntryPoint(), jwtUtil, loginService, cookieUtil)
                        , BasicAuthenticationFilter.class)
                .addFilterAt(
                        new JwtLoginFilter(authenticationManagerBean(), authenticationFailHandler(), mapper, loginService, jwtUtil, cookieUtil)
                        , UsernamePasswordAuthenticationFilter.class)
        ;
    }

    private AccessDeniedHandler accessDeniedException() {
        return new CustomAccessDeniedHandler();
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CustomAuthenticationEntryPoint authenticationEntryPoint(){
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    JwtLoginAuthenticationFailHandler authenticationFailHandler(){
        return new JwtLoginAuthenticationFailHandler();
    }

}
