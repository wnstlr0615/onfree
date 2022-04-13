package com.onfree.common.model;

import lombok.*;

import javax.persistence.Embeddable;


@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UploadFile {
    String uploadFilename;
    String storeFilename;

    //== 생성 메서드 ==//
    public static UploadFile createUploadFile(String uploadFileName, String storeFileName){
        return builder()
                .uploadFilename(uploadFileName)
                .storeFilename(storeFileName)
                .build();

    }
}
