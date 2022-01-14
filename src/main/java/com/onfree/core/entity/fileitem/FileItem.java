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
    private FileType fileType;
}
