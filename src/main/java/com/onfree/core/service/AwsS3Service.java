package com.onfree.core.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.onfree.common.model.UploadFile;
import com.onfree.common.properties.AmazonS3Properties;
import com.onfree.core.entity.fileitem.FileItem;
import com.onfree.core.entity.fileitem.FileType;
import com.onfree.core.repository.FileItemRepository;
import com.onfree.utils.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static com.onfree.common.constant.AWSConstant.S3_PROFILE_IMAGE_PATH;
import static com.onfree.core.entity.fileitem.FileType.PROFILE_IMAGE_FILE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsS3Service {
    private final AmazonS3Client s3;
    private final AmazonS3Properties s3Properties;
    private final FileStore fileStore;
    private final FileItemRepository fileItemRepository;

    /** S3에 프로필 이미지 올리기*/
    @Transactional
    public String s3ProfileImageFileUpload(MultipartFile file){
        final String profileImageSavePathInS3 = getProfileImageSavePathInS3();
        final UploadFile uploadFile = fileStore.saveFile(file);
        final File localFile = fileStore.getFile(uploadFile);

        s3FileUpload(profileImageSavePathInS3, localFile);
        saveFileItem(profileImageSavePathInS3, uploadFile);
        removeLocalFile(localFile);
        return s3.getUrl(profileImageSavePathInS3, localFile.getName()).toString();
    }

    private void removeLocalFile(File localFile) {
        fileStore.removeFile(localFile);
    }

    private void saveFileItem(String profileImageSavePathInS3, UploadFile uploadFile) {
        fileItemRepository.save(
                createFileItem(uploadFile, profileImageSavePathInS3, PROFILE_IMAGE_FILE)
        );
    }

    private FileItem createFileItem(UploadFile uploadFile, String bucketPath, FileType fileType) {
        return FileItem.builder()
                .uploadFile(uploadFile)
                .bucketPath(bucketPath)
                .fileType(fileType)
                .build();
    }

    private String getProfileImageSavePathInS3() {
        return s3Properties.getBucketName() + S3_PROFILE_IMAGE_PATH;
    }

    private void s3FileUpload(String bucketPath , File localFile) {
        final PutObjectRequest objectRequest = new PutObjectRequest(bucketPath, localFile.getName(), localFile);
        objectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
        s3.putObject(objectRequest);
    }
}
