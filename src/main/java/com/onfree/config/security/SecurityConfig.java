package com.onfree.config.security;

import com.onfree.config.security.handler.CustomAuthenticationEntryPoint;
import com.onfree.config.security.handler.JwtLoginAuthenticationFailHandler;
import com.onfree.config.security.filter.JwtCheckFilter;
import com.onfree.config.security.filter.JwtLoginFilter;
import com.onfree.core.service.JWTRefreshTokenService;
import com.onfree.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity()
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomUserDetailService customUserDetailService;
    private final JWTRefreshTokenService jwtRefreshTokenService;
    private final JWTUtil jwtUtil;

    @Override
    public void configure(WebSecurity web) throws Exception {
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
                "/login", "/error", "/logout"
        };
        String[] GETWhiteList = new String[]{
                "/api/notices", "/api/notices/**",
                "/api/questions", "/api/questions/**"
        };
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST,"/api/users/artist", "/api/users/normal").permitAll()
                .antMatchers(whiteList).permitAll()
                .antMatchers(HttpMethod.GET,GETWhiteList).permitAll()
                .antMatchers(HttpMethod.POST, "/api/notices/**", "/api/questions/**").hasRole("ADMIN")
                .antMatchers("/api/users/artist/**").hasRole("ARTIST")
                .antMatchers("/api/users/normal/**").hasRole("NORMAL")
                .anyRequest().authenticated();
        http.httpBasic().disable()
            .csrf().disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
        ;

        http
                .addFilterAt(
                        new JwtCheckFilter(customUserDetailService, authenticationEntryPoint(), jwtRefreshTokenService, jwtUtil)
                        , BasicAuthenticationFilter.class)
                .addFilterAt(
                        new JwtLoginFilter(authenticationManagerBean(), authenticationFailHandler(), jwtRefreshTokenService, jwtUtil)
                        , UsernamePasswordAuthenticationFilter.class)
        ;
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
