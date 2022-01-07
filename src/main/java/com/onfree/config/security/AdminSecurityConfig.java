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
        auth.inMemoryAuthentication().withUser("joon123").password("{noop}1234").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/admin/**")
            .authorizeRequests()
                .antMatchers("/admin/api/notices", "/admin/api/notices/*").hasRole("ADMIN")
                .antMatchers("/admin/api/questions", "/admin/api/questions/*").hasRole("ADMIN")
                .anyRequest().hasRole("ADMIN");
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic()
        ;
    }
}
