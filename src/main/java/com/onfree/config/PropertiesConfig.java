package com.onfree.config;

import com.onfree.common.properties.AmazonS3Properties;
import com.onfree.common.properties.JWTProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {JWTProperties.class, AmazonS3Properties.class})
public class PropertiesConfig {
}
