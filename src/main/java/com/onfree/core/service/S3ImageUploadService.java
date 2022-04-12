package com.onfree.core.service;

import com.onfree.common.error.code.FileErrorCode;
import com.onfree.common.error.exception.FileException;
import com.onfree.common.model.UploadFile;
import com.onfree.core.entity.fileitem.FileItem;
import com.onfree.core.entity.fileitem.FileStatus;
import com.onfree.core.entity.fileitem.FileType;
import com.onfree.core.repository.FileItemRepository;
import com.onfree.utils.AwsS3Component;
import com.onfree.utils.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class S3ImageUploadService {
    private final AwsS3Component awsS3Component;
    private final FileStore fileStore;
    private final FileItemRepository fileItemRepository;

    @Transactional
    public String s3ImageFileUpload(MultipartFile file, FileType fileType){
        //파일 저장
        final UploadFile uploadFile = fileStore.saveFile(file);

        //파일 읽기
        final File localFile = fileStore.getFile(uploadFile);

        //s3에 파일 업로드
        String savePathInS3 = awsS3Component.s3FileUpload(localFile, fileType);

        // FileItem Entity 생성
        FileItem fileItem = FileItem.createFileItem(uploadFile, savePathInS3, fileType, FileStatus.TEMP);

        //FileItem 저장
        fileItemRepository.save(fileItem);

        //LocalFile 제거
        fileStore.removeFile(localFile);

        //파일 저장명 반환
        return uploadFile.getStoreFileName();
    }

    /** 이미지 조회 */
    public URL getFile(String filename) {
        final FileItem fileItem = findFileItemByFileName(filename);
        return awsS3Component.getFile(filename, fileItem.getFileType());
    }

    private FileItem findFileItemByFileName(String filename) {
        return fileItemRepository.findByStoreFileName(filename)
                .orElseThrow(() ->new FileException(FileErrorCode.NOT_FOUND_FILENAME));
    }





}
