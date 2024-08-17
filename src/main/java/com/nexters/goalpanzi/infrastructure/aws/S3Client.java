package com.nexters.goalpanzi.infrastructure.aws;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nexters.goalpanzi.application.upload.ObjectStorageClient;
import com.nexters.goalpanzi.exception.BadRequestException;
import com.nexters.goalpanzi.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.nexters.goalpanzi.exception.ErrorCode.FILE_UPLOAD_FAILED;
import static com.nexters.goalpanzi.exception.ErrorCode.INVALID_FILE;

@RequiredArgsConstructor
@Component
public class S3Client implements ObjectStorageClient {

    @Value("${cloud.aws.credentials.bucket}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    public String uploadFile(final MultipartFile file) {
        String fileObjKeyName = UUID.randomUUID().toString();
        File tempFile = convert(file);

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, tempFile)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(tempFile.length());

            request.setMetadata(metadata);
            amazonS3.putObject(request);

            return amazonS3.getUrl(bucketName, fileObjKeyName).toString();
        } catch (SdkClientException e) {
            throw new BaseException(FILE_UPLOAD_FAILED, e);
        }
    }

    public void deleteFile(final String uploadedFileUrl) {
        if (uploadedFileUrl != null) {
            String fileObjKeyName = uploadedFileUrl.substring(uploadedFileUrl.lastIndexOf("/") + 1);
            amazonS3.deleteObject(bucketName, fileObjKeyName);
        }
    }

    private File convert(final MultipartFile originalFile) {
        try {
            File tempFile = File.createTempFile("temp", null);
            originalFile.transferTo(tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new BadRequestException(INVALID_FILE);
        }
    }
}
