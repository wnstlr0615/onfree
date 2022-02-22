package com.onfree.config.webmvc;

import com.onfree.config.webmvc.resolver.CurrentArtistUserArgumentResolver;
import com.onfree.config.webmvc.resolver.CurrentNormalUserArgumentResolver;
import com.onfree.config.webmvc.resolver.LoginUserArgumentResolver;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentNormalUserArgumentResolver());
        resolvers.add(loginUserArgumentResolver());
        resolvers.add(currentArtistUserArgumentResolver());
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .maxAge(300);
    }

    @Bean
    public CurrentArtistUserArgumentResolver currentArtistUserArgumentResolver() {
        return new CurrentArtistUserArgumentResolver();
    }
    @Bean
    public CurrentNormalUserArgumentResolver currentNormalUserArgumentResolver() {
        return new CurrentNormalUserArgumentResolver();
    }
    @Bean
    public LoginUserArgumentResolver loginUserArgumentResolver() {
        return new LoginUserArgumentResolver();
    }
}
