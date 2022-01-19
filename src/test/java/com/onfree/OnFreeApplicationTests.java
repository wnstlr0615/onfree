package com.onfree;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties =
        "spring.config.location=" +
        "classpath:application.yml" +
        ",classpath:aws.yml")
class OnFreeApplicationTests {

    @Test
    void contextLoads() {
    }

}
