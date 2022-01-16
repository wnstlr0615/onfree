package com.onfree.utils;

import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.exception.GlobalException;
import com.onfree.common.model.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class FileStore {
    private final String LOCAL_PROFILE_IMAGE_PATH = "C:/Users/wnstl/onfreeImage/";

    public String getDirPath() {
        return LOCAL_PROFILE_IMAGE_PATH;
    }

    public UploadFile saveFile(MultipartFile multipartFile)  {
        String originalFileName = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFileName);
        try {
            multipartFile.transferTo(new File(LOCAL_PROFILE_IMAGE_PATH + storeFileName));
        } catch (IOException e) {
            log.error("파일 업로드중 에러가 발생 하였습니다.");
            throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
        return createUploadFile(originalFileName, storeFileName);
    }

    private UploadFile createUploadFile( String originalFileName, String storeFileName) {
        return UploadFile.builder()
                .storeFileName(storeFileName)
                .uploadFileName(originalFileName)
                .build();
    }

    private String createStoreFileName(String originalFileName) {
        final UUID randomUUID = UUID.randomUUID();
        String ext = extractExt(originalFileName);
        return randomUUID + "." + ext;
    }

    private String extractExt(String originalFileName) {
        final int pos = originalFileName.lastIndexOf(".");
        return originalFileName.substring(pos+1);
    }

    public void removeFile(File targetFile) {
        final String targetFileName = targetFile.getName();
        if(targetFile.delete()){
            log.info("{} deleted success", targetFile);
            return;
        }
        log.error("{} deleted fail", targetFile);
    }

    public File getFile(UploadFile uploadFile) {
        return new File(LOCAL_PROFILE_IMAGE_PATH, uploadFile.getStoreFileName());
    }
}