package com.onfree.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.annotation.PostConstruct;
import java.time.Duration;

@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "jwt")
@Setter
@Getter
@ToString
public class JWTProperties {
    private final Duration accessTokenExpiredTime;
    private final Duration refreshTokenExpiredTime;
    private final String secretKey;
}
