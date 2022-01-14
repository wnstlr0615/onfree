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
    String uploadFileName;
    String storeFileName;
}
