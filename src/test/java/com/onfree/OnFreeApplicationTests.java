package com.onfree;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties =
        "spring.config.location=" +
        "classpath:application.yml" +
        ",classpath:aws.yml")
@ActiveProfiles("test")
class OnFreeApplicationTests {

    @Test
    void contextLoads() {
    }

}
