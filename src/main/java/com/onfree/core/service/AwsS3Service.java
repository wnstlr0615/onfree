package com.onfree.core.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.onfree.common.error.code.FileErrorCode;
import com.onfree.common.error.exception.FileException;
import com.onfree.common.model.UploadFile;
import com.onfree.common.properties.AmazonS3Properties;
import com.onfree.core.entity.fileitem.FileItem;
import com.onfree.core.entity.fileitem.FileStatus;
import com.onfree.core.entity.fileitem.FileType;
import com.onfree.core.repository.FileItemRepository;
import com.onfree.utils.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URL;

import static com.onfree.core.entity.fileitem.FileType.PROFILE_IMAGE_FILE;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AwsS3Service {
    private final AmazonS3Client s3;
    private final AmazonS3Properties s3Properties;
    private final FileStore fileStore;
    private final FileItemRepository fileItemRepository;

    /** 이미지 업로드 */
    @Transactional
    public String s3ImageFileUpload(MultipartFile file, FileType type){

        final String profileImageSavePathInS3 = getImageSavePathInS3(type);
        final UploadFile uploadFile = fileStore.saveFile(file);
        final File localFile = fileStore.getFile(uploadFile);

        s3FileUpload(profileImageSavePathInS3, localFile); // s3에 파일 업로드
        saveFileItem(profileImageSavePathInS3, uploadFile, type); // FileItem Entity 저장
        removeLocalFile(localFile); // 로컬 파일 제거
        return uploadFile.getStoreFileName();
    }
    private void removeLocalFile(File localFile) {
        fileStore.removeFile(localFile);
    }

    private void saveFileItem(String profileImageSavePathInS3, UploadFile uploadFile, FileType profileImageFile) {
        fileItemRepository.save(
                createFileItem(uploadFile, profileImageSavePathInS3, profileImageFile, FileStatus.TEMP)
        );
    }

    private FileItem createFileItem(UploadFile uploadFile, String bucketPath, FileType fileType, FileStatus status) {
        return FileItem.createFileItem(uploadFile, bucketPath, fileType, status);
    }

    private String getImageSavePathInS3(FileType fileType) {
        return s3Properties.getBucketName() + fileType.getPath();
    }

    private void s3FileUpload(String bucketPath , File localFile) {
        final PutObjectRequest objectRequest = new PutObjectRequest(bucketPath, localFile.getName(), localFile);
        objectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
        s3.putObject(objectRequest);
    }


    /** 이미지 조회 */
    public URL getFile(String filename) {
        final FileItem fileItem = findFileItemByFileName(filename);
        final String imageSavePathInS3 = getImageSavePathInS3(fileItem.getFileType());
        return s3.getUrl(imageSavePathInS3, filename);
    }

    private FileItem findFileItemByFileName(String filename) {
        return fileItemRepository.findByStoreFileName(filename)
                .orElseThrow(() ->new FileException(FileErrorCode.NOT_FOUND_FILENAME));
    }
}
