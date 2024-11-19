package com.nexters.goalpanzi.infrastructure.aws;

import com.nexters.goalpanzi.config.redis.RedisInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import static com.nexters.goalpanzi.fixture.MissionFixture.IMAGE_FILE;
import static com.nexters.goalpanzi.fixture.MissionFixture.UPLOADED_IMAGE_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(
        initializers = {RedisInitializer.class}
)
class S3ClientTest {

    @MockBean
    private S3Client s3Client;

    @Test
    public void 파일을_업로드한다() {
        when(s3Client.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);

        String uploadedFileUrl = s3Client.uploadFile(IMAGE_FILE);

        assertThat(uploadedFileUrl).isEqualTo(UPLOADED_IMAGE_URL);
    }
}