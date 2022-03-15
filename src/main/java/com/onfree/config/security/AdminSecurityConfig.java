package com.onfree.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@Order(Ordered.LOWEST_PRECEDENCE-1)
@RequiredArgsConstructor
public class AdminSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 임시 계정 생성
        auth.inMemoryAuthentication().withUser("onfree").password("{noop}onfree").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] whiteListOnlyAdmin = {
                "/admin/api/v1/notices/**",
                "/admin/api/v1/questions/**",
                "/admin/api/v1/drawing-fields/**"
        };
        http.antMatcher("/admin/**")
            .authorizeRequests()
                .antMatchers(whiteListOnlyAdmin).hasRole("ADMIN")
                .anyRequest().hasRole("ADMIN");
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic()
        ;
    }
}
