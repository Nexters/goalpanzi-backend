package com.nexters.goalpanzi.common.support;

import com.nexters.goalpanzi.application.upload.ObjectStorageClient;
import com.nexters.goalpanzi.config.RedisInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(
        initializers = {RedisInitializer.class}
)
public class IntegrationTest {

    @MockBean
    public ObjectStorageClient objectStorageClient;
}
