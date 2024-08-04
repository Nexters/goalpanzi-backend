package com.nexters.goalpanzi.application.ncp;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public interface ObjectStorageClient {

    String uploadFile(final MultipartFile file);

    void deleteFile(final String uploadedFileUrl);
}
