package com.nexters.goalpanzi;

import com.nexters.goalpanzi.config.RedisInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(
        initializers = {RedisInitializer.class}
)
class GoalpanziApplicationTests {

    @Test
    void contextLoads() {
    }

}
