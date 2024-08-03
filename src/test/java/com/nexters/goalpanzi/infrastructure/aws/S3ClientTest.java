//package com.nexters.goalpanzi.infrastructure.ncp;
//
//import com.nexters.goalpanzi.infrastructure.aws.S3Client;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//class S3ClientTest {
//
//    @Autowired
//    private S3Client s3Client;
//
//    private File tempFile;
//    private MultipartFile imageFile;
//    private String uploadedFileUrl;
//
//    @BeforeEach
//    void 테스트를_위한_이미지_파일을_생성한다() throws IOException {
//        tempFile = File.createTempFile("test-image", ".jpg");
//
//        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//            fos.write("이미지 파일 내용".getBytes());
//        }
//
//        imageFile = new MockMultipartFile(
//                "file",
//                tempFile.getName(),
//                "image/jpeg",
//                new FileInputStream(tempFile)
//        );
//    }
//
//    @AfterEach
//    public void 테스트_후_업로드된_이미지_파일을_삭제한다() {
//        s3Client.deleteFile(uploadedFileUrl);
//
//        if (tempFile != null && tempFile.exists()) {
//            tempFile.delete();
//        }
//    }
//
//    @Test
//    public void 파일을_업로드한다() {
//        uploadedFileUrl = s3Client.uploadFile(imageFile);
//
//        assertThat(uploadedFileUrl).isNotNull();
//
//        System.out.println(uploadedFileUrl);
//    }
//}