package com.onfree.config;

import com.onfree.core.entity.user.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
       return () -> {
           final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
           if(authentication == null || !authentication.isAuthenticated()){
               return Optional.empty();
           }else if(!(authentication.getPrincipal() instanceof User)){
               return Optional.of("운영자");
           }
           return Optional.of(((User) authentication.getPrincipal()).getUserId().toString());
       };
    }
}
