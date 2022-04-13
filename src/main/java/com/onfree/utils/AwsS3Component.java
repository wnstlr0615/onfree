package com.onfree.utils;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.onfree.common.error.code.FileErrorCode;
import com.onfree.common.error.exception.FileException;
import com.onfree.common.properties.AmazonS3Properties;
import com.onfree.core.entity.fileitem.FileItem;
import com.onfree.core.entity.fileitem.FileType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Component {
    private final AmazonS3Client s3;
    private final AmazonS3Properties s3Properties;

    public String getFilePathByFileType(FileType fileType) {
        return s3Properties.getBucketName() + fileType.getPath();
    }

    public String s3FileUpload(File localFile, FileType fileType) {
        String savePath = getFilePathByFileType(fileType);
        try {
            final PutObjectRequest objectRequest = new PutObjectRequest(savePath, localFile.getName(), localFile);
            objectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
            s3.putObject(objectRequest);
        } catch (SdkClientException e) {
            log.error("aws s3에 파일 업로드중 에러 발생 - filename : {}, fileType : {}, errorMessage : {}",localFile.getName(), fileType.name() , e.getMessage());
            throw new FileException(FileErrorCode.FILE_UPLOAD_ERROR);
        }
        return savePath;
    }

    public URL getFileUrl(FileItem fileItem){
        String filePath = getFilePathByFileType(fileItem.getFileType());
        try {
            return s3.getUrl(filePath, fileItem.getUploadFile().getStoreFilename());
        } catch (Exception e) {
            log.error("aws s3로 부터 파일 로드 중 에러 발생 - filename : {}, fileType : {}, errorMessage : {}", fileItem.getUploadFile().getUploadFilename(), fileItem.getFileType().name() , e.getMessage());
            e.printStackTrace();
            throw new FileException(FileErrorCode.LOAD_FILE_ERROR);
        }
    }
}
