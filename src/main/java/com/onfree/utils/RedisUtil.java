package com.onfree.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate redisTemplate;

    public void addData(String key, String value, Duration timeout){
        redisTemplate.opsForValue().set(key, value, timeout);
    }
    public void addData(String key, String value){
        redisTemplate.opsForValue().set(key, value);
    }

    public String getData(String key){
        return redisTemplate.opsForValue().get(key);
    }
    public void deleteData(String key){
        redisTemplate.opsForValue().getOperations().delete(key);
    }
}
