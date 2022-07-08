package com.onfree.core.entity.fileitem;

import com.onfree.common.model.BaseEntity;
import com.onfree.common.model.UploadFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class FileItem extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileItemId;

    @Embedded
    private UploadFile uploadFile;

    private String bucketPath;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @Enumerated(EnumType.STRING)
    private FileStatus status;

    //== 생성 메서드 ==//
    public static FileItem createFileItem(UploadFile uploadFile, String bucketPath, FileType fileType, FileStatus status) {
        return FileItem.builder()
                .uploadFile(uploadFile)
                .bucketPath(bucketPath)
                .fileType(fileType)
                .status(status)
                .build();
    }

    //== 비즈니스 메서드 ==//
    public String getFilePath(){
        return bucketPath + "/" + uploadFile.getStoreFilename();
    }

    public void deleted(){
        status = FileStatus.DELETED_TEMP;
    }
}
