package com.onfree.config.webmvc;

import com.onfree.config.webmvc.resolver.CurrentArtistUserArgumentResolver;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(getArtistUserArgumentResolver());
    }

    @Bean
    public CurrentArtistUserArgumentResolver getArtistUserArgumentResolver() {
        return new CurrentArtistUserArgumentResolver();
    }
}
