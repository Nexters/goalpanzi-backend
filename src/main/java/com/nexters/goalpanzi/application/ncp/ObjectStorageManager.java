package com.nexters.goalpanzi.application.ncp;

import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageManager {
    
    String uploadFile(final MultipartFile file);

    void deleteFile(final String uploadedFileUrl);
}
