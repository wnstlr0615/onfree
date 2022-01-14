package com.onfree.common.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "cloud.aws")
@Getter
@ToString
public class AmazonS3Properties {
    private final String bucketName;
    private final String accessKey;
    private final String secretKey;
    private final String region;
}
