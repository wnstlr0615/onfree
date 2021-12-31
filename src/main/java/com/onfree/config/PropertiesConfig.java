package com.onfree.config;

import com.onfree.properties.JWTProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = JWTProperties.class)
public class PropertiesConfig {
}
