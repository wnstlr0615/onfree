package com.onfree.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.onfree.common.properties.AmazonS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AmazonS3Config {
    private final AmazonS3Properties amazonS3Properties;

    @Bean
    public AmazonS3Client amazonS3Client(){
        return (AmazonS3Client) AmazonS3Client.builder()
                .withRegion(amazonS3Properties.getRegion())
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(amazonS3Properties.getAccessKey(), amazonS3Properties.getSecretKey())
                        )
                ).build();
    }
}
