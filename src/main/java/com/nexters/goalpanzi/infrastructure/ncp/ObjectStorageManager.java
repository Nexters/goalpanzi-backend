package com.nexters.goalpanzi.infrastructure.ncp;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.nexters.goalpanzi.exception.BadRequestException;
import com.nexters.goalpanzi.exception.BaseException;
import jakarta.annotation.PostConstruct;
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
public class ObjectStorageManager {

    @Value("${cloud.aws.credentials.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;

    private TransferManager tm;

    @PostConstruct
    private void buildTransferManager() {
        tm = TransferManagerBuilder.standard().withS3Client(s3Client).build();
    }

    public String uploadFile(final MultipartFile file) {
        String fileObjKeyName = UUID.randomUUID().toString();
        try {
            Upload upload = tm.upload(bucketName, fileObjKeyName, convert(file));
            upload.waitForCompletion();

            return s3Client.getUrl(bucketName, fileObjKeyName).toString();
        } catch (SdkClientException | InterruptedException e) {
            throw new BaseException(FILE_UPLOAD_FAILED);
        }
    }

    public void deleteFile(final String uploadedFileUrl) {
        if (uploadedFileUrl != null) {
            String fileObjKeyName = uploadedFileUrl.substring(uploadedFileUrl.lastIndexOf("/") + 1);
            s3Client.deleteObject(bucketName, fileObjKeyName);
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
